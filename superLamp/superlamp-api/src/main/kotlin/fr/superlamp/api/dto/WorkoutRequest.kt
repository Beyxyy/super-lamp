package fr.superlamp.api.dto

/**
 * DTO pour la création/mise à jour d'un Workout (requête HTTP)
 */
data class WorkoutRequest(
    val splitId: Long,
    val name: String,
    val description: String? = null,
    val order: Int
)
