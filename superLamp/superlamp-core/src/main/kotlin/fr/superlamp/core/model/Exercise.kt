package fr.superlamp.core.model

/**
 * Modèle métier représentant un exercise (sans annotations JPA)
 */
data class Exercise(
    val id: Long? = null,
    val name: String,
    val description: String? = null,
    val muscleGroup: String? = null
)
