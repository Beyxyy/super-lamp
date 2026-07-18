package fr.superlamp.data.mapper

import fr.superlamp.core.model.Set as SetModel
import fr.superlamp.data.entity.Lift as LiftEntity
import fr.superlamp.data.entity.Set as SetEntity
import fr.superlamp.data.entity.WorkoutExercise as WorkoutExerciseEntity

/**
 * Mapper entre SetEntity (JPA) et Set (modèle métier)
 */
fun SetEntity.toModel(): SetModel = SetModel(
    id = this.id,
    liftId = this.lift.id!!,
    workoutExerciseId = this.workoutExercise.id!!,
    order = this.order,
    reps = this.reps,
    weightKg = this.weightKg,
    restTimeSeconds = this.restTimeSeconds,
    notes = this.notes,
    createdAt = this.createdAt
)

fun SetModel.toEntity(lift: LiftEntity, workoutExercise: WorkoutExerciseEntity): SetEntity = SetEntity(
    id = this.id,
    lift = lift,
    workoutExercise = workoutExercise,
    order = this.order,
    reps = this.reps,
    weightKg = this.weightKg,
    restTimeSeconds = this.restTimeSeconds,
    notes = this.notes,
    createdAt = this.createdAt
)
