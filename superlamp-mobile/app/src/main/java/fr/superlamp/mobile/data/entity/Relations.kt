package fr.superlamp.mobile.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import java.time.LocalDateTime

/** Programme + nombre de séances types. */
data class SplitSummary(
    @Embedded val split: Split,
    val workoutCount: Int,
)

/** Séance type + nom de son programme. */
data class WorkoutWithSplit(
    @Embedded val workout: Workout,
    @ColumnInfo(name = "split_name") val splitName: String,
)

/** Exercice planifié + infos de l'exercice. */
data class WorkoutExerciseDetail(
    @Embedded val workoutExercise: WorkoutExercise,
    @ColumnInfo(name = "exercise_name") val exerciseName: String,
    @ColumnInfo(name = "exercise_muscle_group") val muscleGroup: String?,
)

/** Séance réalisée + agrégats de ses séries. */
data class LiftSummary(
    @Embedded val lift: Lift,
    val setCount: Int,
    val exerciseCount: Int,
    val volume: Double,
)

/** Série + nom de l'exercice. */
data class LiftSetDetail(
    @Embedded val set: LiftSet,
    @ColumnInfo(name = "exercise_name") val exerciseName: String,
)

/** Série + séance dans laquelle elle a été faite (historique d'un exercice). */
data class ExerciseSetHistory(
    @Embedded val set: LiftSet,
    @ColumnInfo(name = "lift_start_time") val liftStartTime: LocalDateTime,
    @ColumnInfo(name = "lift_name") val liftName: String,
)
