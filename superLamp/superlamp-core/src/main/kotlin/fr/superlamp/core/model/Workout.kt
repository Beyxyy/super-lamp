package fr.superlamp.core.model

/**
 * Modèle métier représentant un workout (sans annotations JPA)
 * Utilisé par la couche Core pour la logique métier
 */
data class Workout(
    val id: Long? = null,
    val splitId: Long,
    val name: String,
    val description: String? = null,
    val order: Int
)
