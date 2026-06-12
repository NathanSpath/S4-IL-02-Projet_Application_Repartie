# Compte Rendu — Projet Application Répartie
## Partie RMI et Réservation de Restaurant

**Membres du binôme :**
* Enzo Marchal
* Julien Vincent

**Classe :** S4-IL-02 — Licence Informatique
**Année universitaire :** 2025 – 2026

---

## 1. Introduction

Ce document présente la partie du projet dont j'ai eu la responsabilité : la mise en place du mécanisme **RMI (Remote Method Invocation)** permettant la communication entre le serveur proxy et les serveurs de restaurant, ainsi que la fonctionnalité de réservation de table côté frontend. 

L'application a pour objectif de proposer une carte interactive de Nancy avec, entre autres, la possibilité de réserver une table dans un restaurant directement depuis l'interface web.

### Périmètre couvert
Le périmètre couvert par ce compte rendu comprend :
* Les interfaces et implémentations RMI (`dossier rmi`)
* Les programmes de démarrage (`dossier launcher`)
* Les classes du modèle métier (`dossier model`)
* Les services Java encapsulant la logique applicative
* La couche d'accès aux données (DAO)
* La partie frontend limitée au formulaire de réservation (HTML et JavaScript)

---

## 2. Fonctionnement global de Java RMI

Java RMI est un mécanisme qui permet à deux programmes Java, s'exécutant sur des machines différentes, de s'appeler mutuellement comme s'ils étaient dans le même programme.

Concrètement, quand le serveur proxy veut déclencher une réservation sur le serveur restaurant, il appelle simplement une méthode Java. C'est RMI qui se charge de tout le travail réseau en coulisses :
1. **Sérialisation** des paramètres.
2. **Transmission** sur le réseau.
3. **Désérialisation** côté récepteur.
4. **Exécution** de la méthode sur le véritable objet cible.
5. **Renvoi** du résultat (valeur de retour ou exception) dans le sens inverse.

> 💡 **Transparence réseau :** Du point de vue du code appelant, la complexité réseau est totalement invisible. L'appelant travaille avec un **stub** (un objet Java local qui se comporte exactement comme l'objet distant, mais dont les méthodes envoient en réalité des messages sur le réseau).

Ce stub est obtenu en interrogeant le **registre RMI**, un annuaire qui tourne par défaut sur le **port 1099** et dans lequel les serveurs publient leurs objets sous un nom unique.

L'intérêt majeur dans ce projet est de séparer proprement la logique métier (accès à la base Oracle, vérification des disponibilités, création des réservations) dans son propre processus serveur. Le proxy HTTP n'a pas à connaître les détails de la base de données : il interroge RMI et reçoit une réponse JSON.

---

## 3. Organisation du code RMI

Le code RMI est structuré en quatre couches distinctes qui se superposent. Le modèle métier fournit les objets de données partagés entre toutes ces couches.

```
┌────────────────────────────────────────┐
│     Interfaces Distantes (Contrat)     │
└───────────────────┬────────────────────┘
                    ▼
┌────────────────────────────────────────┐
│            Implémentations             │
└───────────────────┬────────────────────┘
                    ▼
┌────────────────────────────────────────┐
│          Services Appli (Logique)      │
└───────────────────┬────────────────────┘
                    ▼
┌────────────────────────────────────────┐
│          Couche DAO (Données)          │
└────────────────────────────────────────┘
```

### 3.1 Les interfaces distantes : le contrat

Deux interfaces définissent ce que les serveurs RMI exposent. Toutes deux héritent de `java.rmi.Remote` (obligatoire pour RMI) et toutes leurs méthodes déclarent `throws RemoteException` afin de signaler les erreurs réseau potentielles.

#### ServiceRestaurant
C'est l'interface principale. Elle déclare quatre méthodes fondamentales :
* `getRestaurants()` : retourne la liste de tous les restaurants sous forme de chaîne JSON.
* `getTables()` : retourne la liste de toutes les tables disponibles, également en JSON.
* `creerReservation()` : reçoit les informations du client (nom, prénom, téléphone), l'identifiant de la table, le nombre de personnes ainsi que la durée, et retourne un booléen (`true`/`false`) en JSON selon le résultat.
* `getReservations()` : retourne les réservations existantes d'un client identifié par ses coordonnées.

*Note architecturale : Le choix de retourner des chaînes JSON plutôt que des objets Java sérialisés est délibéré. Cela rend le proxy totalement indépendant des types métier du module RMI, et les données peuvent être transmises telles quelles au navigateur sans conversion supplémentaire.*

#### ServiceDistributeur
C'est l'interface du service central. Elle ne déclare qu'une seule méthode :
* `enregistrerClient()` : permet à un serveur de restaurant de transmettre son stub au service central lors de son démarrage. C'est la procédure de *check-in* : le restaurant annonce sa présence et le service central l'ajoute à sa liste de serveurs actifs.

### 3.2 Les implémentations

#### ServiceRestaurantImpl
Cette classe réalise concrètement l'interface `ServiceRestaurant`. Elle hérite de `UnicastRemoteObject`, ce qui l'exporte automatiquement comme objet distant dès son instanciation. Les trois services métier (`ClientService`, `RestaurantService`, `ReservationService`) lui sont passés par **injection de dépendances** via le constructeur.

* **Déroulement de `creerReservation()` :** Lorsqu'elle est appelée via RMI depuis le proxy, elle commence par identifier le client grâce à `ClientService.getClient()` (qui crée automatiquement un nouveau client en base s'il n'existe pas encore). Elle construit ensuite un objet `Reservation` horodaté à l'instant courant, puis le confie à `ReservationService.addReservation()`.
* **Gestion des erreurs :** Si tout se passe bien, `true` est sérialisé en JSON et retourné. Si une erreur survient (table inexistante, capacité insuffisante, conflit horaire), une `RemoteException` est levée avec le message d'origine, qui remonte jusqu'au proxy pour être transmis au navigateur.

#### ServiceCentral
Cette classe réalise l'interface `ServiceDistributeur`. Elle maintient en mémoire une liste de stubs `ServiceRestaurant` correspondant aux serveurs enregistrés. La méthode `enregistrerClient()` est déclarée `synchronized` pour éviter les problèmes de concurrence et d'accès concurrentiel (race conditions) si deux serveurs tentaient de s'enregistrer simultanément.

---

## 4. Démarrage des serveurs RMI

La séquence de démarrage est critique car l'ensemble du mécanisme d'annuaire en dépend.

```
Step 1: [LancerServiceCentral] (Port 1099) -> Crée le registre & Publie "RMI.rmi.ServiceCentral"
                                    ▲
                                    │ (Enregistrement / Check-in)
Step 2: [LancerServiceRestaurant] ──┘ -> Instancie Services/DAO & S'enregistre auprès du Central
```

1. **LancerServiceCentral (À exécuter en premier) :** Il crée le registre RMI sur le port `1099` de la machine locale, instancie un `ServiceCentral` et le publie dans ce registre sous le nom `"RMI.rmi.ServiceCentral"`. Dès cet instant, le service central est repérable sur le réseau.
2. **LancerServiceRestaurant (À exécuter en second) :** Lancé avec deux arguments (un nom de service comme `"restaurant_nancy_1"` et l'IP du service central). Ce programme instancie la chaîne complète d'objets (`Requete`, les trois services métier, puis `ServiceRestaurantImpl`). Il publie ensuite son propre stub dans le registre RMI local sous le nom fourni en argument, puis se connecte au registre du service central pour appeler `enregistrerClient()`.

Ce mécanisme permet d'avoir plusieurs serveurs de restaurant indépendants, chacun gérant sa propre base de données, tout en étant centralisés et connus du service central.

---

## 6. Le formulaire de réservation côté frontend

### 6.1 Structure HTML
Dans le fichier `index.html`, la barre latérale gauche de l'interface contient le formulaire de réservation. Il requiert cinq informations clés de l'utilisateur :
* Nom
* Prénom
* Numéro de téléphone (avec validation native à 10 chiffres via l'attribut `pattern="[0-9]{10}"`)
* Numéro de table
* Nombre de couverts

### 6.2 Soumission et traitement JavaScript
Un écouteur d'événement (`addEventListener`) est attaché à l'événement `submit` du formulaire :
1. **Interception :** Appel à `event.preventDefault()` pour bloquer le rechargement par défaut de la page.
2. **Encodage & Construction :** Extraction des valeurs des champs et encodage via `encodeURIComponent()` afin de sécuriser la transmission des caractères spéciaux (accents, espaces).
3. **Transmission HTTP :** Envoi d'une requête `GET` asynchrone via l'API `fetch()` vers l'URL : `http://localhost:8080/api/reservations?paramètres...`
4. **Analyse de la réponse JSON :**
   * **`true` :** Réservation réussie. Le formulaire est réinitialisé avec `reset()` et une notification verte s'affiche.
   * **Présence d'une propriété `erreur` :** Problème côté serveur (conflit horaire, table pleine). La notification rouge reprend le message d'erreur exact.
   * **Autre cas :** Affichage d'un message d'échec générique.

### 6.3 La notification utilisateur
La fonction `afficherPopup()` gère dynamiquement l'affichage :
* Crée un élément `<div>` injecté et positionné en haut au centre de la page.
* **Stylisation dédiée :** Fond vert (`#4CAF50`) pour un succès, rouge (`#f44336`) pour un échec. Ombre portée et texte blanc pour maximiser la lisibilité.
* **Disparition :** Au bout de 3 secondes, une transition CSS sur l'opacité (`opacity`) fait disparaître progressivement l'élément avant son retrait complet du DOM.

*Avantage UX : Cette approche évite l'utilisation de `alert()` qui bloque le thread principal de l'interface utilisateur. De plus, si l'utilisateur clique frénétiquement sur le bouton, la fonction supprime automatiquement la notification précédente avant d'afficher la nouvelle, évitant l'empilement d'éléments.*

---

## 7. Flux complet d'une réservation

Voici le parcours de bout en bout d'une action de réservation :

1. **Frontend (Navigateur) :** L'utilisateur valide le formulaire ➔ Le JS intercepte, encode et effectue un `fetch()` en `GET` vers le proxy HTTP.
2. **Proxy HTTP :** Le `ReservationHandler` intercepte la requête, valide la présence des paramètres, puis délègue à `OpenData.reserverTable()`.
3. **Passerelle RMI (Proxy -> Serveur) :** Le proxy invoque la méthode `creerReservation()` sur le **stub RMI** du serveur restaurant concerné.
4. **Serveur Restaurant (Logique Métier) :** * `ServiceRestaurantImpl` reçoit l'appel distant.
   * Appelle `ClientService.getClient()` (récupération ou création du client).
   * Instancie l'objet `Reservation` horodaté.
   * Transmet l'objet à `ReservationService.addReservation()`.
5. **Couche Données (DAO & Base Oracle) :** Le service ouvre une connexion **JDBC transactionnelle**, effectue les contrôles de cohérence (existence de la table, capacité d'accueil, chevauchements horaires en SQL/Java). Si tout est valide, il insère la ligne et effectue un `commit`. En cas de problème, un `rollback` automatique est exécuté.
6. **Retour d'information :** Le résultat (`true`/`false` ou l'erreur métier propagée) remonte le tunnel RMI sous forme de JSON jusqu'au proxy, qui le renvoie au navigateur. Le script JS met à jour l'interface avec la notification visuelle appropriée.

---

## 8. Conclusion

La partie RMI réalisée met en place une **architecture répartie fonctionnelle et découplée** où le serveur de restaurant est un processus Java indépendant communiquant via un contrat d'interface rigoureux. La séparation en couches (`Interface` / `Implémentation` / `Service` / `DAO`) garantit une excellente maintenabilité, chaque niveau ayant une responsabilité unique.

La robustesse du système repose sur des **validations multiniveaux** (Java et SQL) et sur l'utilisation de **transactions JDBC** avec Rollback automatique, prémunissant l'application contre toute corruption ou réservation partielle en base de données.

Côté Frontend, l'implémentation asynchrone asynchronise idéalement l'expérience utilisateur : la carte de Nancy reste interactive, et le retour d'information par popups non bloquantes fluidifie grandement l'utilisation globale de l'application.