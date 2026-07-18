package fr.superlamp.data.mapper

import fr.superlamp.core.model.Lift as LiftModel
import fr.superlamp.data.entity.Lift as LiftEntity
import fr.superlamp.data.entity.Workout as WorkoutEntity

/**
 * Mapper entre LiftEntity (JPA) et Lift (modèle métier)
 */
fun LiftEntity.toModel(): LiftModel = LiftModel(
    id = this.id,
    name = this.name,
    description = this.description,
    userId = this.userId,
    workoutId = this.workout.id!!,
    startTime = this.startTime,
    endTime = this.endTime
)

fun LiftModel.toEntity(workout: WorkoutEntity): LiftEntity = LiftEntity(
    id = this.id,
    name = this.name,
    description = this.description,
    userId = this.userId,
    workout = workout,
    startTime = this.startTime,
    endTime = this.endTime
)
