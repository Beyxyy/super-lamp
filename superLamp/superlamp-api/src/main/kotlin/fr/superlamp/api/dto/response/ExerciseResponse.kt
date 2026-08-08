package fr.superlamp.api.dto

/**
 * DTO pour la réponse HTTP contenant un Exercise
 */
data class ExerciseResponse(
    val id: Long?,
    val name: String,
    val description: String?,
    val muscleGroup: String?
)
