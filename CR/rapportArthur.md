# Rapport sur la Conception et l'Implémentation de la Couche d'Accès aux Données (BDD & RMI)

Ce document détaille la conception et l'implémentation de la couche d'accès aux données de l'application, incluant la modélisation de la base de données, le modèle transactionnel, la couche DAO (Data Access Object) et l'intégration avec les objets métier Java. Cette partie du projet constitue le fondement de la persistance des données et de leur manipulation sécurisée.

## 1. Conception de la Base de Données

### 1.1. Modèle Entité-Relation
La base de données a été conçue pour supporter les entités principales de l'application de gestion de restaurant : `Clients`, `Restaurants`, `Tables` et `Réservations`.

*   **Clients:** Stocke les informations des utilisateurs (ID, Nom, Prénom, Numéro de Téléphone).
    *   **Justification du type de Numéro de Téléphone (String):** Initialement, le numéro de téléphone a été envisagé comme un entier (`int`). Cependant, pour des raisons de robustesse et de conformité aux bonnes pratiques, il a été implémenté comme une chaîne de caractères (`String`). Cette décision est cruciale pour plusieurs raisons :
        *   **Préservation des zéros initiaux:** Les numéros de téléphone peuvent commencer par un '0' (ex: `0612345678`), qui serait perdu si stocké comme un entier.
        *   **Flexibilité du format:** Permet de stocker des numéros avec des préfixes internationaux (+33), des espaces, des tirets ou d'autres caractères non numériques, sans contraintes de formatage strictes.
        *   **Éviter les erreurs d'octal:** En Java, un littéral entier commençant par '0' est interprété comme un nombre octal (base 8). Cela peut causer des erreurs de compilation ou d'exécution si des chiffres non octaux (8, 9) sont présents dans le numéro.
        *   **Prévention des dépassements de capacité:** Les numéros de téléphone longs peuvent dépasser la capacité maximale d'un type `int` ou `long`.
*   **Restaurants:** Contient les détails des établissements (ID, Nom, Adresse, Coordonnées géographiques).
*   **Tables:** Représente les tables au sein des restaurants (ID, ID du Restaurant, Numéro de Table, Nombre de Places). Une clé étrangère (`IDRES`) lie chaque table à un restaurant, assurant l'intégrité référentielle.
*   **Réservations:** Enregistre les réservations effectuées (ID, ID du Client, ID de la Table, Nombre de Convives, Durée, Date et Heure de la Réservation). Des clés étrangères (`IDCLI`, `IDTAB`) lient les réservations aux clients et aux tables.

### 1.2. Schéma de la Base de Données (Exemple de tables Oracle)
Le schéma de la base de données est adapté pour un environnement Oracle, comme en témoignent les requêtes utilisant `VARCHAR2` et `NUMTODSINTERVAL`.

## 2. Modèle Transactionnel et Intégrité des Données

La gestion des transactions est un pilier fondamental pour garantir la cohérence et l'intégrité des données, surtout dans un environnement où plusieurs opérations peuvent être effectuées simultanément ou en cascade.

### 2.1. Transactions Atomiques
Chaque opération de modification de la base de données (ajout, mise à jour, suppression) est conçue pour être atomique. Cela signifie que soit toutes les étapes de l'opération sont complétées avec succès (transaction `COMMIT`), soit aucune ne l'est (transaction `ROLLBACK`). Cette approche prévient les états incohérents de la base de données en cas d'erreur ou d'interruption.

### 2.2. Détection de Conflits de Réservation
Un mécanisme spécifique a été mis en place dans la méthode `addReservation` de la classe `Requete` pour prévenir les chevauchements de réservations sur une même table. Lors de la tentative d'ajout d'une nouvelle réservation, une requête SQL est exécutée pour vérifier l'existence de réservations conflictuelles :
