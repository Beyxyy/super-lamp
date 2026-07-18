package fr.superlamp.api.dto

/**
 * DTO pour la réponse HTTP contenant un Workout
 */
data class WorkoutResponse(
    val id: Long?,
    val splitId: Long,
    val name: String,
    val description: String?,
    val order: Int
)
