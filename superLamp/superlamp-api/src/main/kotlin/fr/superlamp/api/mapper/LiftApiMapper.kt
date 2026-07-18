package fr.superlamp.api.mapper

import fr.superlamp.api.dto.LiftRequest
import fr.superlamp.api.dto.LiftResponse
import fr.superlamp.core.model.Lift

/**
 * Mapper entre LiftRequest/LiftResponse et Lift
 */
fun LiftRequest.toModel(): Lift = Lift(
    name = this.name,
    description = this.description,
    userId = this.userId,
    workoutId = this.workoutId,
    startTime = this.startTime,
    endTime = this.endTime
)

fun Lift.toResponse(): LiftResponse = LiftResponse(
    id = this.id,
    name = this.name,
    description = this.description,
    userId = this.userId,
    workoutId = this.workoutId,
    startTime = this.startTime,
    endTime = this.endTime
)
