package fr.superlamp.mobile.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import fr.superlamp.mobile.data.dao.ExerciseDao
import fr.superlamp.mobile.data.dao.LiftDao
import fr.superlamp.mobile.data.dao.LiftSetDao
import fr.superlamp.mobile.data.dao.SplitDao
import fr.superlamp.mobile.data.dao.WorkoutDao
import fr.superlamp.mobile.data.dao.WorkoutExerciseDao
import fr.superlamp.mobile.data.entity.Exercise
import fr.superlamp.mobile.data.entity.Lift
import fr.superlamp.mobile.data.entity.LiftSet
import fr.superlamp.mobile.data.entity.Split
import fr.superlamp.mobile.data.entity.Workout
import fr.superlamp.mobile.data.entity.WorkoutExercise

@Database(
    entities = [
        Split::class,
        Workout::class,
        Exercise::class,
        WorkoutExercise::class,
        Lift::class,
        LiftSet::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class SuperLampDatabase : RoomDatabase() {
    abstract fun splitDao(): SplitDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutExerciseDao(): WorkoutExerciseDao
    abstract fun liftDao(): LiftDao
    abstract fun liftSetDao(): LiftSetDao

    companion object {
        fun build(context: Context): SuperLampDatabase =
            Room.databaseBuilder(context, SuperLampDatabase::class.java, "superlamp.db")
                .addCallback(SeedCallback)
                .build()
    }
}

/** Catalogue d'exercices de départ, inséré à la création de la base. */
private object SeedCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        DEFAULT_EXERCISES.forEach { (name, muscleGroup) ->
            db.execSQL("INSERT INTO exercise (name, muscle_group) VALUES (?, ?)", arrayOf(name, muscleGroup))
        }
    }
}

val MUSCLE_GROUPS = listOf("Pectoraux", "Dos", "Épaules", "Bras", "Jambes", "Fessiers", "Mollets", "Abdos")

private val DEFAULT_EXERCISES = listOf(
    "Développé couché" to "Pectoraux",
    "Développé incliné haltères" to "Pectoraux",
    "Écarté à la poulie" to "Pectoraux",
    "Dips" to "Pectoraux",
    "Traction" to "Dos",
    "Rowing barre" to "Dos",
    "Rowing haltère" to "Dos",
    "Tirage vertical" to "Dos",
    "Soulevé de terre" to "Dos",
    "Développé militaire" to "Épaules",
    "Élévation latérale" to "Épaules",
    "Élévation poulie" to "Épaules",
    "Reverse fly" to "Épaules",
    "Face pull" to "Épaules",
    "Curl biceps" to "Bras",
    "Curl marteau" to "Bras",
    "Extension triceps poulie" to "Bras",
    "Barre au front" to "Bras",
    "Squat" to "Jambes",
    "Presse à cuisses" to "Jambes",
    "Fente bulgare" to "Jambes",
    "Leg extension" to "Jambes",
    "Leg curl" to "Jambes",
    "Soulevé de terre roumain" to "Jambes",
    "Hip thrust" to "Fessiers",
    "Mollets debout" to "Mollets",
    "Crunch à la poulie" to "Abdos",
    "Relevé de jambes" to "Abdos",
)
