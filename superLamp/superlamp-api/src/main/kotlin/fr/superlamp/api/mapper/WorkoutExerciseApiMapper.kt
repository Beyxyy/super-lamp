package fr.superlamp.api.mapper

import fr.superlamp.api.dto.WorkoutExerciseRequest
import fr.superlamp.api.dto.WorkoutExerciseResponse
import fr.superlamp.core.model.WorkoutExercise

/**
 * Mapper entre WorkoutExerciseRequest/WorkoutExerciseResponse et WorkoutExercise
 */
fun WorkoutExerciseRequest.toModel(): WorkoutExercise = WorkoutExercise(
    workoutId = this.workoutId,
    exerciseId = this.exerciseId,
    order = this.order,
    plannedSets = this.plannedSets,
    plannedReps = this.plannedReps,
    restTimeSeconds = this.restTimeSeconds
)

fun WorkoutExercise.toResponse(): WorkoutExerciseResponse = WorkoutExerciseResponse(
    id = this.id,
    workoutId = this.workoutId,
    exerciseId = this.exerciseId,
    order = this.order,
    plannedSets = this.plannedSets,
    plannedReps = this.plannedReps,
    restTimeSeconds = this.restTimeSeconds
)
