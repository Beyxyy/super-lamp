package fr.superlamp.api.dto

/**
 * DTO pour la création/mise à jour d'un Split
 */
data class SplitRequest(
    val name: String,
    val description: String? = null
)
