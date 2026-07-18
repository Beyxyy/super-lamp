package fr.superlamp.core.model

/**
 * Modèle métier représentant un split (sans annotations JPA)
 */
data class Split(
    val id: Long? = null,
    val name: String,
    val description: String? = null
)
