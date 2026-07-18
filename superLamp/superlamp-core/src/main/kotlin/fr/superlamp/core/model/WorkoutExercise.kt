package fr.superlamp.core.model

/**
 * Modèle métier représentant un workout_exercise (sans annotations JPA)
 */
data class WorkoutExercise(
    val id: Long? = null,
    val workoutId: Long,
    val exerciseId: Long,
    val order: Int,
    val plannedSets: Int,
    val plannedReps: Int? = null,
    val restTimeSeconds: Int? = null
)
