# superlamp-api Module

Ce module contient les contrôleurs REST de l'application.

## Endpoints disponibles

### Workouts

#### GET /api/workouts
Retourne la liste de tous les workouts.

**Réponse:**
```json
[
  {
    "id": 1,
    "name": "Morning Run",
    "description": "5km run in the park",
    "startTime": "2024-03-06T08:00:00",
    "endTime": "2024-03-06T08:30:00",
    "durationSeconds": 1800,
    "caloriesBurned": 300
  }
]
```

#### GET /api/workouts/{id}
Retourne un workout spécifique.

**Paramètres:**
- `id` (Long) : ID du workout

**Réponse:**
```json
{
  "id": 1,
  "name": "Morning Run",
  "description": "5km run in the park",
  "startTime": "2024-03-06T08:00:00",
  "endTime": "2024-03-06T08:30:00",
  "durationSeconds": 1800,
  "caloriesBurned": 300
}
```

#### POST /api/workouts
Crée un nouveau workout.

**Corps de la requête:**
```json
{
  "name": "Morning Run",
  "description": "5km run in the park",
  "startTime": "2024-03-06T08:00:00",
  "endTime": "2024-03-06T08:30:00",
  "durationSeconds": 1800,
  "caloriesBurned": 300
}
```

**Réponse:**
```json
{
  "id": 1,
  "name": "Morning Run",
  "description": "5km run in the park",
  "startTime": "2024-03-06T08:00:00",
  "endTime": "2024-03-06T08:30:00",
  "durationSeconds": 1800,
  "caloriesBurned": 300
}
```

#### PUT /api/workouts/{id}
Met à jour un workout existant.

**Paramètres:**
- `id` (Long) : ID du workout

**Corps de la requête:**
```json
{
  "name": "Updated Morning Run",
  "description": "5km run in the park",
  "startTime": "2024-03-06T08:00:00",
  "endTime": "2024-03-06T08:30:00",
  "durationSeconds": 1800,
  "caloriesBurned": 300
}
```

**Réponse:**
```json
{
  "id": 1,
  "name": "Updated Morning Run",
  "description": "5km run in the park",
  "startTime": "2024-03-06T08:00:00",
  "endTime": "2024-03-06T08:30:00",
  "durationSeconds": 1800,
  "caloriesBurned": 300
}
```

#### DELETE /api/workouts/{id}
Supprime un workout.

**Paramètres:**
- `id` (Long) : ID du workout

**Réponse:**
204 No Content

## Console H2

La console H2 est disponible à l'adresse: `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:superlamp`
- User: `sa`
- Password: (laisser vide)
