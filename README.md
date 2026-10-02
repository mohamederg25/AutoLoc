# AutoLoc

API REST de gestion d'une agence de location de véhicules, réalisée dans le cadre
des ateliers ASI (Spring Boot, Maven et persistance des données avec Spring Data JPA).

**Auteur :** Mohamed Erguez

## Stack technique

| Élément | Choix |
|---|---|
| Langage | Java 17 |
| Framework | Spring Boot |
| Build | Maven (wrapper `mvnw` inclus) |
| Persistance | Spring Data JPA / Hibernate |
| Base de données | MySQL (`autoloc_db`) |
| Outillage | Lombok, Spring Validation, Spring DevTools |

## Structure du projet

```
src/main/java/tn/esprit/autoloc/
├── domain/         # Entités JPA et énumérations
├── repository/     # Interfaces JpaRepository
├── service/        # Logique métier
└── controller/     # Contrôleurs REST
```

## Configuration

La connexion à la base se fait dans `src/main/resources/application.properties`.
Le schéma est généré automatiquement par Hibernate (`spring.jpa.hibernate.ddl-auto=update`),
il suffit donc que la base `autoloc_db` existe.

## Lancer le projet

```bash
./mvnw spring-boot:run
```

L'application démarre sur `http://localhost:8080`.

Pour vérifier que tout compile et que le contexte Spring se charge correctement :

```bash
./mvnw test
```

## Avancement des ateliers

- ✅ **Chapitre 2 — Spring Boot, Maven et Spring Data JPA** : modèle de persistance
  complet, sans associations.
  Entités : `Vehicule`, `Agence`, `Client`, `Contrat`, `Employe`, `Equipement`,
  `Maintenance`, `Paiement`, `Reservation`.
  Énumérations : `CategorieVehicule`, `StatutVehicule`, `StatutReservation`,
  `ModePaiement`, `RoleEmploye`.
