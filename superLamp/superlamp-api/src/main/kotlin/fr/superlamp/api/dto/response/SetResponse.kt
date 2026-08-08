package fr.superlamp.api.dto

import java.time.LocalDateTime

/**
 * DTO pour la réponse HTTP contenant un Set
 */
data class SetResponse(
    val id: Long?,
    val liftId: Long,
    val workoutExerciseId: Long,
    val order: Int,
    val reps: Int,
    val weightKg: Float?,
    val restTimeSeconds: Int?,
    val notes: String?,
    val createdAt: LocalDateTime
)
