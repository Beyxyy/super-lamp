package fr.superlamp.api.mapper

import fr.superlamp.api.dto.ExerciseRequest
import fr.superlamp.api.dto.ExerciseResponse
import fr.superlamp.core.model.Exercise

/**
 * Mapper entre ExerciseRequest/ExerciseResponse et Exercise
 */
fun ExerciseRequest.toModel(): Exercise = Exercise(
    name = this.name,
    description = this.description,
    muscleGroup = this.muscleGroup
)

fun Exercise.toResponse(): ExerciseResponse = ExerciseResponse(
    id = this.id,
    name = this.name,
    description = this.description,
    muscleGroup = this.muscleGroup
)
