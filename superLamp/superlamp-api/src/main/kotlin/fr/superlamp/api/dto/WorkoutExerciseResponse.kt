package fr.superlamp.api.dto

/**
 * DTO pour la réponse HTTP contenant un WorkoutExercise
 */
data class WorkoutExerciseResponse(
    val id: Long?,
    val workoutId: Long,
    val exerciseId: Long,
    val order: Int,
    val plannedSets: Int,
    val plannedReps: Int?,
    val restTimeSeconds: Int?
)
