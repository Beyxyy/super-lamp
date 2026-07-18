package fr.superlamp.core.model

import java.time.LocalDateTime

/**
 * Modèle métier représentant un set (sans annotations JPA)
 */
data class Set(
    val id: Long? = null,
    val liftId: Long,
    val workoutExerciseId: Long,
    val order: Int,
    val reps: Int,
    val weightKg: Float? = null,
    val restTimeSeconds: Int? = null,
    val notes: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
