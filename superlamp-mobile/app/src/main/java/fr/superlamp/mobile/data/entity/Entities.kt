package fr.superlamp.mobile.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/*
 * Mêmes tables et colonnes que les entités JPA du backend (superlamp-data),
 * avec deux ajustements pour un usage mobile mono-utilisateur :
 * - pas de user_id sur lift ;
 * - l'historique (lift, lift_set) survit à la modification des programmes :
 *   lift.workout_id et lift_set.workout_exercise_id passent à NULL au lieu de
 *   supprimer les séances, et lift_set référence directement l'exercice.
 */

/** Programme d'entraînement, ex. « PPL » ou « Full body ». */
@Entity(
    tableName = "split",
    indices = [Index(value = ["name"], unique = true)],
)
data class Split(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(collate = ColumnInfo.NOCASE) val name: String,
    val description: String? = null,
)

/** Séance type d'un programme, ex. « Push ». */
@Entity(
    tableName = "workout",
    foreignKeys = [
        ForeignKey(
            entity = Split::class,
            parentColumns = ["id"],
            childColumns = ["split_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("split_id")],
)
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "split_id") val splitId: Long,
    val name: String,
    val description: String? = null,
    @ColumnInfo(name = "workout_order") val order: Int,
)

/** Exercice du catalogue. */
@Entity(
    tableName = "exercise",
    indices = [Index(value = ["name"], unique = true)],
)
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(collate = ColumnInfo.NOCASE) val name: String,
    val description: String? = null,
    @ColumnInfo(name = "muscle_group") val muscleGroup: String? = null,
)

/** Exercice planifié dans une séance type (séries, reps et repos visés). */
@Entity(
    tableName = "workout_exercise",
    foreignKeys = [
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["id"],
            childColumns = ["workout_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("workout_id"), Index("exercise_id")],
)
data class WorkoutExercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "workout_id") val workoutId: Long,
    @ColumnInfo(name = "exercise_id") val exerciseId: Long,
    @ColumnInfo(name = "exercise_order") val order: Int,
    @ColumnInfo(name = "planned_sets") val plannedSets: Int,
    @ColumnInfo(name = "planned_reps") val plannedReps: Int? = null,
    @ColumnInfo(name = "rest_time_seconds") val restTimeSeconds: Int? = null,
)

/** Séance réalisée. end_time à NULL = séance en cours. */
@Entity(
    tableName = "lift",
    foreignKeys = [
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["id"],
            childColumns = ["workout_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("workout_id"), Index("start_time")],
)
data class Lift(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String? = null,
    @ColumnInfo(name = "workout_id") val workoutId: Long? = null,
    @ColumnInfo(name = "start_time") val startTime: LocalDateTime,
    @ColumnInfo(name = "end_time") val endTime: LocalDateTime? = null,
)

/** Série réalisée pendant une séance (`Set` côté backend, table lift_set). */
@Entity(
    tableName = "lift_set",
    foreignKeys = [
        ForeignKey(
            entity = Lift::class,
            parentColumns = ["id"],
            childColumns = ["lift_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = WorkoutExercise::class,
            parentColumns = ["id"],
            childColumns = ["workout_exercise_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("lift_id"), Index("exercise_id"), Index("workout_exercise_id")],
)
data class LiftSet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "lift_id") val liftId: Long,
    @ColumnInfo(name = "exercise_id") val exerciseId: Long,
    @ColumnInfo(name = "workout_exercise_id") val workoutExerciseId: Long? = null,
    @ColumnInfo(name = "set_order") val order: Int,
    val reps: Int,
    @ColumnInfo(name = "weight_kg") val weightKg: Double? = null,
    @ColumnInfo(name = "rest_time_seconds") val restTimeSeconds: Int? = null,
    val notes: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: LocalDateTime = LocalDateTime.now(),
)
