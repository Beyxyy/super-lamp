# superlamp-data Module

Ce module contient la couche de données de l'application avec JPA/Hibernate.

## Structure

```
superlamp-data/src/main/kotlin/fr/superlamp/data/
├── config/          # Configurations JPA
├── entity/          # Entités JPA
├── repository/      # Répositories Spring Data
└── service/         # Services métier
```

## Configuration

Le module utilise:
- **Spring Data JPA** pour l'accès aux données
- **H2 Database** en mode in-memory pour le développement
- **Hibernate** comme implémentation JPA

## Entité Workout

L'entité `Workout` est définie avec:
- `id` : Clé primaire auto-générée (stratégie IDENTITY)
- `name` : Nom du workout (obligatoire)
- `description` : Description optionnelle
- `startTime` : Heure de début (obligatoire)
- `endTime` : Heure de fin (optionnelle)
- `durationSeconds` : Durée en secondes (optionnelle)
- `caloriesBurned` : Calories brûlées (optionnelle)

## Utilisation

Pour utiliser ce module dans votre application:

1. Ajoutez la configuration JPA dans votre application principale:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:superlamp
    username: sa
    password: 
    driver-class-name: org.h2.Driver
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
    database-platform: org.hibernate.dialect.H2Dialect
```

2. Injectez le `WorkoutService` dans vos contrôleurs:
```kotlin
@RestController
class WorkoutController(private val workoutService: WorkoutService) {
    // Votre logique métier ici
}
```

## Accès à la console H2

La console H2 est disponible à l'adresse: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:superlamp`
- User: `sa`
- Password: (laisser vide)
