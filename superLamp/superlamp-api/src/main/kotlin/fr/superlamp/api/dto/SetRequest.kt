package fr.superlamp.api.dto

import java.time.LocalDateTime

/**
 * DTO pour la création/mise à jour d'un Set
 */
data class SetRequest(
    val liftId: Long,
    val workoutExerciseId: Long,
    val order: Int,
    val reps: Int,
    val weightKg: Float? = null,
    val restTimeSeconds: Int? = null,
    val notes: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
