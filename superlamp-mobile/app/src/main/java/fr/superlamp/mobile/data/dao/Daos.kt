package fr.superlamp.mobile.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import fr.superlamp.mobile.data.entity.Exercise
import fr.superlamp.mobile.data.entity.ExerciseSetHistory
import fr.superlamp.mobile.data.entity.Lift
import fr.superlamp.mobile.data.entity.LiftSet
import fr.superlamp.mobile.data.entity.LiftSetDetail
import fr.superlamp.mobile.data.entity.LiftSummary
import fr.superlamp.mobile.data.entity.Split
import fr.superlamp.mobile.data.entity.SplitSummary
import fr.superlamp.mobile.data.entity.Workout
import fr.superlamp.mobile.data.entity.WorkoutExercise
import fr.superlamp.mobile.data.entity.WorkoutExerciseDetail
import fr.superlamp.mobile.data.entity.WorkoutWithSplit
import kotlinx.coroutines.flow.Flow

@Dao
interface SplitDao {
    @Query(
        """
        SELECT s.*, (SELECT COUNT(*) FROM workout w WHERE w.split_id = s.id) AS workoutCount
        FROM split s ORDER BY s.name
        """
    )
    fun observeSummaries(): Flow<List<SplitSummary>>

    @Query("SELECT * FROM split WHERE id = :id")
    fun observeById(id: Long): Flow<Split?>

    @Query("SELECT * FROM split WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): Split?

    @Insert
    suspend fun insert(split: Split): Long

    @Update
    suspend fun update(split: Split)

    @Delete
    suspend fun delete(split: Split)
}

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout WHERE split_id = :splitId ORDER BY workout_order")
    fun observeBySplit(splitId: Long): Flow<List<Workout>>

    @Query("SELECT * FROM workout WHERE split_id = :splitId ORDER BY workout_order")
    suspend fun getBySplit(splitId: Long): List<Workout>

    @Query("SELECT * FROM workout WHERE id = :id")
    fun observeById(id: Long): Flow<Workout?>

    @Query(
        """
        SELECT w.*, s.name AS split_name FROM workout w
        JOIN split s ON s.id = w.split_id
        ORDER BY s.name, w.workout_order
        """
    )
    fun observeAllWithSplit(): Flow<List<WorkoutWithSplit>>

    @Query("SELECT COALESCE(MAX(workout_order), -1) FROM workout WHERE split_id = :splitId")
    suspend fun maxOrder(splitId: Long): Int

    @Insert
    suspend fun insert(workout: Workout): Long

    @Update
    suspend fun update(workout: Workout)

    @Update
    suspend fun updateAll(workouts: List<Workout>)

    @Delete
    suspend fun delete(workout: Workout)
}

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercise ORDER BY name")
    fun observeAll(): Flow<List<Exercise>>

    @Query("SELECT * FROM exercise WHERE id = :id")
    fun observeById(id: Long): Flow<Exercise?>

    @Query("SELECT * FROM exercise WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): Exercise?

    @Insert
    suspend fun insert(exercise: Exercise): Long

    @Update
    suspend fun update(exercise: Exercise)

    @Delete
    suspend fun delete(exercise: Exercise)
}

@Dao
interface WorkoutExerciseDao {
    @Query(
        """
        SELECT we.*, e.name AS exercise_name, e.muscle_group AS exercise_muscle_group
        FROM workout_exercise we
        JOIN exercise e ON e.id = we.exercise_id
        WHERE we.workout_id = :workoutId
        ORDER BY we.exercise_order
        """
    )
    fun observeDetails(workoutId: Long): Flow<List<WorkoutExerciseDetail>>

    @Query("SELECT * FROM workout_exercise WHERE workout_id = :workoutId ORDER BY exercise_order")
    suspend fun getByWorkout(workoutId: Long): List<WorkoutExercise>

    @Query("SELECT * FROM workout_exercise WHERE workout_id = :workoutId AND exercise_id = :exerciseId LIMIT 1")
    suspend fun find(workoutId: Long, exerciseId: Long): WorkoutExercise?

    @Query("SELECT COALESCE(MAX(exercise_order), -1) FROM workout_exercise WHERE workout_id = :workoutId")
    suspend fun maxOrder(workoutId: Long): Int

    @Insert
    suspend fun insert(workoutExercise: WorkoutExercise): Long

    @Update
    suspend fun update(workoutExercise: WorkoutExercise)

    @Update
    suspend fun updateAll(workoutExercises: List<WorkoutExercise>)

    @Delete
    suspend fun delete(workoutExercise: WorkoutExercise)
}

@Dao
interface LiftDao {
    @Query(
        """
        SELECT l.*,
               COUNT(s.id) AS setCount,
               COUNT(DISTINCT s.exercise_id) AS exerciseCount,
               COALESCE(SUM(s.reps * COALESCE(s.weight_kg, 0)), 0) AS volume
        FROM lift l
        LEFT JOIN lift_set s ON s.lift_id = l.id
        GROUP BY l.id
        ORDER BY l.start_time DESC
        """
    )
    fun observeSummaries(): Flow<List<LiftSummary>>

    @Query("SELECT * FROM lift WHERE id = :id")
    fun observeById(id: Long): Flow<Lift?>

    @Query("SELECT * FROM lift WHERE end_time IS NULL ORDER BY start_time DESC LIMIT 1")
    fun observeActive(): Flow<Lift?>

    @Query("SELECT * FROM lift WHERE workout_id IS NOT NULL ORDER BY start_time DESC LIMIT 1")
    fun observeLastWithWorkout(): Flow<Lift?>

    /** Clôt les séances restées ouvertes à l'heure de leur dernière série. */
    @Query(
        """
        UPDATE lift SET end_time = COALESCE(
            (SELECT MAX(s.created_at) FROM lift_set s WHERE s.lift_id = lift.id),
            start_time
        )
        WHERE end_time IS NULL
        """
    )
    suspend fun closeActiveLifts()

    @Insert
    suspend fun insert(lift: Lift): Long

    @Update
    suspend fun update(lift: Lift)

    @Delete
    suspend fun delete(lift: Lift)
}

@Dao
interface LiftSetDao {
    @Query(
        """
        SELECT s.*, e.name AS exercise_name FROM lift_set s
        JOIN exercise e ON e.id = s.exercise_id
        WHERE s.lift_id = :liftId
        ORDER BY s.id
        """
    )
    fun observeDetails(liftId: Long): Flow<List<LiftSetDetail>>

    /** Séries de l'exercice lors de la dernière séance antérieure à [liftId]. */
    @Query(
        """
        SELECT * FROM lift_set
        WHERE exercise_id = :exerciseId AND lift_id = (
            SELECT s.lift_id FROM lift_set s
            JOIN lift l ON l.id = s.lift_id
            WHERE s.exercise_id = :exerciseId
              AND l.start_time < (SELECT start_time FROM lift WHERE id = :liftId)
            ORDER BY l.start_time DESC
            LIMIT 1
        )
        ORDER BY set_order
        """
    )
    suspend fun getPreviousSets(exerciseId: Long, liftId: Long): List<LiftSet>

    @Query("SELECT COALESCE(MAX(set_order), 0) FROM lift_set WHERE lift_id = :liftId AND exercise_id = :exerciseId")
    suspend fun maxOrder(liftId: Long, exerciseId: Long): Int

    @Query(
        """
        SELECT s.*, l.start_time AS lift_start_time, l.name AS lift_name FROM lift_set s
        JOIN lift l ON l.id = s.lift_id
        WHERE s.exercise_id = :exerciseId
        ORDER BY l.start_time DESC, s.set_order
        """
    )
    fun observeHistory(exerciseId: Long): Flow<List<ExerciseSetHistory>>

    @Insert
    suspend fun insert(set: LiftSet): Long

    @Update
    suspend fun update(set: LiftSet)

    @Delete
    suspend fun delete(set: LiftSet)
}
