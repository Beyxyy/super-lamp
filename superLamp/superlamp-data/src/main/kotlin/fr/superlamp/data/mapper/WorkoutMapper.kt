package fr.superlamp.data.mapper

import fr.superlamp.core.model.Workout as WorkoutModel
import fr.superlamp.data.entity.Split as SplitEntity
import fr.superlamp.data.entity.Workout as WorkoutEntity

/**
 * Mapper entre WorkoutEntity (JPA) et Workout (modèle métier)
 */
fun WorkoutEntity.toModel(): WorkoutModel = WorkoutModel(
    id = this.id,
    splitId = this.split.id!!,
    name = this.name,
    description = this.description,
    order = this.order
)

fun WorkoutModel.toEntity(split: SplitEntity): WorkoutEntity = WorkoutEntity(
    id = this.id,
    split = split,
    name = this.name,
    description = this.description,
    order = this.order
)
