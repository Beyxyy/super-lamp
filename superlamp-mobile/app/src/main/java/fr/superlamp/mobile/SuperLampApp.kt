package fr.superlamp.mobile

import android.app.Application
import android.content.Context
import fr.superlamp.mobile.data.SuperLampDatabase
import fr.superlamp.mobile.data.repository.ExerciseRepository
import fr.superlamp.mobile.data.repository.LiftRepository
import fr.superlamp.mobile.data.repository.ProgramRepository

class SuperLampApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

/** Injection de dépendances manuelle : une seule base SQLite pour toute l'app. */
class AppContainer(context: Context) {
    private val database = SuperLampDatabase.build(context)

    val exerciseRepository = ExerciseRepository(database.exerciseDao(), database.liftSetDao())

    val programRepository = ProgramRepository(
        database.splitDao(),
        database.workoutDao(),
        database.workoutExerciseDao(),
        database.liftDao(),
    )

    val liftRepository = LiftRepository(database, exerciseRepository)
}
