package fr.superlamp.api.dto

import java.time.LocalDateTime

/**
 * DTO pour la réponse HTTP contenant un Lift
 */
data class LiftResponse(
    val id: Long?,
    val name: String,
    val description: String?,
    val userId: Long,
    val workoutId: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?
)
