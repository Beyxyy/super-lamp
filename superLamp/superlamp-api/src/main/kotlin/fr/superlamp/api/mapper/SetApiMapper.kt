package fr.superlamp.api.mapper

import fr.superlamp.api.dto.SetRequest
import fr.superlamp.api.dto.SetResponse
import fr.superlamp.core.model.Set

/**
 * Mapper entre SetRequest/SetResponse et Set
 */
fun SetRequest.toModel(): Set = Set(
    liftId = this.liftId,
    workoutExerciseId = this.workoutExerciseId,
    order = this.order,
    reps = this.reps,
    weightKg = this.weightKg,
    restTimeSeconds = this.restTimeSeconds,
    notes = this.notes,
    createdAt = this.createdAt
)

fun Set.toResponse(): SetResponse = SetResponse(
    id = this.id,
    liftId = this.liftId,
    workoutExerciseId = this.workoutExerciseId,
    order = this.order,
    reps = this.reps,
    weightKg = this.weightKg,
    restTimeSeconds = this.restTimeSeconds,
    notes = this.notes,
    createdAt = this.createdAt
)
