package fr.superlamp.api.dto

/**
 * DTO pour la réponse HTTP contenant un Split
 */
data class SplitResponse(
    val id: Long?,
    val name: String,
    val description: String?
)
