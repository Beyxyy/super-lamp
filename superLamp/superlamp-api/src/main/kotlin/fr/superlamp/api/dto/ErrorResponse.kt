package fr.superlamp.api.dto

import java.time.Instant

/**
 * DTO pour les réponses d'erreur HTTP
 */
data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: Instant = Instant.now(),
    val path: String? = null
)
