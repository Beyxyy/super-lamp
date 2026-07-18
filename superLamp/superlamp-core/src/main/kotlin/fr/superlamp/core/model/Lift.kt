package fr.superlamp.core.model

import java.time.LocalDateTime

/**
 * Modèle métier représentant un lift (sans annotations JPA)
 */
data class Lift(
    val id: Long? = null,
    val name: String,
    val description: String? = null,
    val userId: Long,
    val workoutId: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime? = null
)
