# Compte Rendu Technique : Architecture du Proxy Web / RMI

L'objectif de cette implémentation est de fournir une interface unique et sécurisée (un point d'entrée central) pour une application web front-end (carte Leaflet), en contournant les restrictions de sécurité des navigateurs (CORS) et en unifiant des sources de données hétérogènes (API REST externes et services RMI internes).

## 1. Le Serveur Central (Le "Chef d'Orchestre")
**Fichier associé :** `ServeurProxy.java`

Le cœur du système repose sur la classe native `HttpServer` de Java. Ce serveur agit comme un routeur d'aiguillage. Son rôle est strictement limité à l'écoute sur le port `8080` et à la distribution des requêtes entrantes vers les contrôleurs appropriés.

| Route (URL) | Rôle | Contrôleur assigné |
| :--- | :--- | :--- |
| `/api/incidents` | Récupération des travaux (Open Data) | `IncidentsHandler` |
| `/api/restaurants` | Liste globale des restaurants | `RestaurantHandler` |
| `/api/tables` | Liste des tables disponibles | `TablesHandler` |
| `/api/liste-reservations` | Historique des réservations | `ListeReservationHandler` |
| `/api/reservations` | Création d'une réservation | `ReservationHandler` |

Cette séparation stricte garantit un code maintenable et permet à plusieurs développeurs de travailler en parallèle sur différentes routes sans générer de conflits Git complexes.

---

## 2. Les Handlers (Les "Contrôleurs")
**Fichiers associés :** Package `handlers/`

Chaque route est gérée par une classe implémentant l'interface `HttpHandler`. Ces classes agissent comme des contrôleurs (similaires au modèle MVC). Elles ont trois responsabilités majeures :

* **La gestion de la sécurité (CORS) :** Elles injectent systématiquement les en-têtes `Access-Control-Allow-Origin: *` et le type de contenu `application/json` pour que le navigateur web autorise la lecture des données.
* **Le traitement des paramètres :** (Particulièrement dans `ReservationHandler`). Le système extrait la chaîne de requête (Query String), la découpe et la convertit en données typées (entiers pour le nombre de personnes, `Timestamp` pour les dates).
* **La gestion des erreurs :** Les blocs `try/catch` garantissent que si une erreur réseau ou une donnée mal formatée survient, le serveur ne plante pas. Il renvoie un code HTTP approprié (ex: `400 Bad Request` pour des paramètres manquants, `500 Internal Server Error` pour un crash distant) accompagné d'un message JSON propre.

---

## 3. Le Service de Données (Le "Moteur")
**Fichier associé :** `OpenData.java`

C'est la pièce maîtresse du proxy. Cette classe encapsule toute la logique de récupération de la donnée. Elle masque la complexité des protocoles au reste de l'application en gérant deux flux totalement distincts :

### Flux A : Requêtes HTTP externes (Open Data)
Pour contourner la politique de sécurité du navigateur client, le proxy endosse le rôle de client HTTP.
1. Il utilise `java.net.http.HttpClient` pour interroger l'API de la Métropole du Grand Nancy (de manière synchrone).
2. *(Mécanisme prévu pour traverser le pare-feu institutionnel via `ProxySelector` si exécuté sur le réseau interne).*
3. Il utilise la bibliothèque `org.json` pour parser la réponse brute, isoler le tableau d'incidents, et reconstruire un nouveau JSON allégé. Il extrait notamment la chaîne `polyline` pour la scinder en deux variables distinctes (`latitude` et `longitude` de type `double`), ce qui mâchera le travail du framework Leaflet côté front-end.

### Flux B : Requêtes RMI internes
Pour les données métiers (restaurants, réservations), le proxy change de casquette et devient un **Client RMI**.
1. Il contacte l'annuaire RMI local sur le port `1099` via `Naming.lookup()`.
2. Il appelle de manière transparente les méthodes de l'interface partagée `ServiceRestaurant`.
3. Il récupère les chaînes JSON déjà formatées par la couche d'accès aux données (DAO) du serveur RMI.