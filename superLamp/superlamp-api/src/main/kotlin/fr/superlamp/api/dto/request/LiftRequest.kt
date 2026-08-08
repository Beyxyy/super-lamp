package fr.superlamp.api.dto

import java.time.LocalDateTime

/**
 * DTO pour la création/mise à jour d'un Lift
 */
data class LiftRequest(
    val name: String,
    val description: String? = null,
    val userId: Long,
    val workoutId: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime? = null
)
