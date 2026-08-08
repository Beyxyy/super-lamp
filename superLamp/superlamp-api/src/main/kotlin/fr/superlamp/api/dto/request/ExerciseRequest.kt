package fr.superlamp.api.dto

/**
 * DTO pour la création/mise à jour d'un Exercise
 */
data class ExerciseRequest(
    val name: String,
    val description: String? = null,
    val muscleGroup: String? = null
)
