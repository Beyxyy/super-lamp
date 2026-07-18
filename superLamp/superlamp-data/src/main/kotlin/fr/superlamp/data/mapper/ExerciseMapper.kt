package fr.superlamp.data.mapper

import fr.superlamp.core.model.Exercise as ExerciseModel
import fr.superlamp.data.entity.Exercise as ExerciseEntity

/**
 * Mapper entre ExerciseEntity (JPA) et Exercise (modèle métier)
 */
fun ExerciseEntity.toModel(): ExerciseModel = ExerciseModel(
    id = this.id,
    name = this.name,
    description = this.description,
    muscleGroup = this.muscleGroup
)

fun ExerciseModel.toEntity(): ExerciseEntity = ExerciseEntity(
    id = this.id,
    name = this.name,
    description = this.description,
    muscleGroup = this.muscleGroup
)
