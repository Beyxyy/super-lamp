package fr.superlamp.mobile.data.repository

import fr.superlamp.mobile.core.cleanOrNull
import fr.superlamp.mobile.data.dao.ExerciseDao
import fr.superlamp.mobile.data.dao.LiftSetDao
import fr.superlamp.mobile.data.entity.Exercise
import fr.superlamp.mobile.data.entity.ExerciseSetHistory
import kotlinx.coroutines.flow.Flow

/** Équivalent mobile de `ExerciseCoreService`. */
class ExerciseRepository(
    private val exerciseDao: ExerciseDao,
    private val liftSetDao: LiftSetDao,
) {
    fun observeAll(): Flow<List<Exercise>> = exerciseDao.observeAll()

    fun observeById(id: Long): Flow<Exercise?> = exerciseDao.observeById(id)

    fun observeHistory(exerciseId: Long): Flow<List<ExerciseSetHistory>> = liftSetDao.observeHistory(exerciseId)

    suspend fun create(name: String, muscleGroup: String?, description: String?): Exercise {
        val cleanName = name.trim()
        require(cleanName.isNotBlank()) { "Le nom de l'exercice est obligatoire" }
        require(exerciseDao.findByName(cleanName) == null) { "L'exercice « $cleanName » existe déjà" }
        val exercise = Exercise(
            name = cleanName,
            muscleGroup = muscleGroup.cleanOrNull(),
            description = description.cleanOrNull(),
        )
        return exercise.copy(id = exerciseDao.insert(exercise))
    }

    suspend fun update(exercise: Exercise, name: String, muscleGroup: String?, description: String?) {
        val cleanName = name.trim()
        require(cleanName.isNotBlank()) { "Le nom de l'exercice est obligatoire" }
        val homonym = exerciseDao.findByName(cleanName)
        require(homonym == null || homonym.id == exercise.id) { "L'exercice « $cleanName » existe déjà" }
        exerciseDao.update(
            exercise.copy(
                name = cleanName,
                muscleGroup = muscleGroup.cleanOrNull(),
                description = description.cleanOrNull(),
            )
        )
    }

    suspend fun delete(exercise: Exercise) = exerciseDao.delete(exercise)

    /** Retrouve un exercice par son nom (sans tenir compte de la casse) ou le crée. */
    suspend fun findOrCreate(name: String): Exercise =
        exerciseDao.findByName(name.trim()) ?: create(name, null, null)
}
