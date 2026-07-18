package fr.superlamp.data.mapper

import fr.superlamp.core.model.WorkoutExercise as WorkoutExerciseModel
import fr.superlamp.data.entity.Exercise as ExerciseEntity
import fr.superlamp.data.entity.Workout as WorkoutEntity
import fr.superlamp.data.entity.WorkoutExercise as WorkoutExerciseEntity

/**
 * Mapper entre WorkoutExerciseEntity (JPA) et WorkoutExercise (modèle métier)
 */
fun WorkoutExerciseEntity.toModel(): WorkoutExerciseModel = WorkoutExerciseModel(
    id = this.id,
    workoutId = this.workout.id!!,
    exerciseId = this.exercise.id!!,
    order = this.order,
    plannedSets = this.plannedSets,
    plannedReps = this.plannedReps,
    restTimeSeconds = this.restTimeSeconds
)

fun WorkoutExerciseModel.toEntity(workout: WorkoutEntity, exercise: ExerciseEntity): WorkoutExerciseEntity = WorkoutExerciseEntity(
    id = this.id,
    workout = workout,
    exercise = exercise,
    order = this.order,
    plannedSets = this.plannedSets,
    plannedReps = this.plannedReps,
    restTimeSeconds = this.restTimeSeconds
)
