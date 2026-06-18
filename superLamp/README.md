# SuperLamp - Application de suivi de workouts

## Architecture

Le projet est structuré en 3 modules:

```
superLamp/
├── superlamp-api/          # Module API REST (Spring Boot)
├── superlamp-core/         # Module core (logique métier commune)
└── superlamp-data/        # Module données (JPA/Hibernate)
```

## Configuration JPA/Hibernate

Le projet utilise **Spring Data JPA** avec **Hibernate** comme implémentation.

### Base de données

**H2 Database** en mode in-memory est configuré pour le développement.

Configuration dans `superlamp-api/src/main/resources/application.yml`:
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

### Accès à la console H2

La console H2 est disponible à l'adresse: `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:superlamp`
- User: `sa`
- Password: (laisser vide)

## Entité Workout

L'entité `Workout` est définie dans `superlamp-data/src/main/kotlin/fr/superlamp/data/entity/Workout.kt`:

```kotlin
@Entity
@Table(name = "workouts")
class Workout(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false)
    val name: String,
    
    @Column
    val description: String? = null,
    
    @Column(name = "start_time", nullable = false)
    val startTime: LocalDateTime,
    
    @Column(name = "end_time")
    val endTime: LocalDateTime? = null,
    
    @Column(name = "duration_seconds")
    val durationSeconds: Long? = null,
    
    @Column(name = "calories_burned")
    val caloriesBurned: Int? = null
)
```

### Champs de l'entité

- `id` : Clé primaire auto-générée (stratégie IDENTITY)
- `name` : Nom du workout (obligatoire)
- `description` : Description optionnelle
- `startTime` : Heure de début (obligatoire)
- `endTime` : Heure de fin (optionnelle)
- `durationSeconds` : Durée en secondes (optionnelle)
- `caloriesBurned` : Calories brûlées (optionnelle)

## Services

### WorkoutService

Le service `WorkoutService` est défini dans `superlamp-data/src/main/kotlin/fr/superlamp/data/service/WorkoutService.kt`:

```kotlin
@Service
@Transactional
class WorkoutService(private val workoutRepository: WorkoutRepository) {
    
    fun createWorkout(workout: Workout): Workout
    fun getWorkoutById(id: Long): Workout?
    fun getAllWorkouts(): List<Workout>
    fun updateWorkout(id: Long, workout: Workout): Workout?
    fun deleteWorkout(id: Long)
}
```

## API REST

### Endpoints disponibles

- `GET /api/workouts` - Liste tous les workouts
- `GET /api/workouts/{id}` - Récupère un workout par ID
- `POST /api/workouts` - Crée un nouveau workout
- `PUT /api/workouts/{id}` - Met à jour un workout
- `DELETE /api/workouts/{id}` - Supprime un workout

Voir `superlamp-api/README.md` pour plus de détails sur l'API.

## Dépendances

### superlamp-data

```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.h2database:h2")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
```

## Exécution

```bash
# Lancer l'application
./gradlew bootRun

# Build le projet
./gradlew build

# Lancer les tests
./gradlew test
```

## Structure des modules

### superlamp-data

```
superlamp-data/src/main/kotlin/fr/superlamp/data/
├── config/          # Configurations JPA
├── entity/          # Entités JPA
├── repository/      # Répositories Spring Data
└── service/         # Services métier
```

### superlamp-api

```
superlamp-api/src/main/kotlin/fr/superlamp/api/
├── controller/      # Contrôleurs REST
└── SuperlampApiApplication.kt
```

## Configuration Spring Boot

Le plugin Spring Boot est configuré dans chaque module:

```kotlin
plugins {
    kotlin("jvm") version "2.2.21"
    id("org.springframework.boot") version "4.0.3"
    id("io.spring.dependency-management") version "1.1.7"
}
```

## Gestion des transactions

Les transactions sont gérées par Spring avec l'annotation `@Transactional` sur le service.

```kotlin
@Service
@Transactional
class WorkoutService(...) {
    // Toutes les méthodes sont transactionnelles
}
```

## Migration vers une base de données réelle

Pour utiliser une base de données réelle comme PostgreSQL:

1. Remplacer la configuration dans `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/superlamp
    username: your_username
    password: your_password
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

2. Ajouter la dépendance PostgreSQL dans `superlamp-data/build.gradle.kts`:
```kotlin
implementation("org.postgresql:postgresql")
```

3. Supprimer ou commenter la dépendance H2:
```kotlin
// implementation("com.h2database:h2")
```
