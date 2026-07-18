package fr.superlamp.api.mapper

import fr.superlamp.api.dto.WorkoutRequest
import fr.superlamp.api.dto.WorkoutResponse
import fr.superlamp.core.model.Workout

/**
 * Mapper entre WorkoutRequest/WorkoutResponse (DTOs) et Workout (modèle métier)
 */
fun WorkoutRequest.toModel(): Workout = Workout(
    splitId = this.splitId,
    name = this.name,
    description = this.description,
    order = this.order
)

fun Workout.toResponse(): WorkoutResponse = WorkoutResponse(
    id = this.id,
    splitId = this.splitId,
    name = this.name,
    description = this.description,
    order = this.order
)
