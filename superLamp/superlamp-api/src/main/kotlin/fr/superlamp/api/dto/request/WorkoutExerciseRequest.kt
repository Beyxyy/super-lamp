package fr.superlamp.api.dto

/**
 * DTO pour la création/mise à jour d'un WorkoutExercise
 */
data class WorkoutExerciseRequest(
    val workoutId: Long,
    val exerciseId: Long,
    val order: Int,
    val plannedSets: Int,
    val plannedReps: Int? = null,
    val restTimeSeconds: Int? = null
)
