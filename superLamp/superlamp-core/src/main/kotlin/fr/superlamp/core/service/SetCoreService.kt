package fr.superlamp.core.service

import fr.superlamp.core.exception.SetNotFoundException
import fr.superlamp.core.model.Set
import fr.superlamp.core.ports.SetRepositoryPort

/**
 * Service métier pour la gestion des Sets
 */
class SetCoreService(
    private val setRepository: SetRepositoryPort
) {
    fun createSet(set: Set): Set {
        require(set.liftId > 0) { "Lift ID must be valid" }
        require(set.workoutExerciseId > 0) { "WorkoutExercise ID must be valid" }
        require(set.reps > 0) { "Reps must be positive" }
        require(set.order >= 0) { "Order must be positive" }
        
        return setRepository.save(set)
    }

    fun getSetById(id: Long): Set {
        return setRepository.findById(id)
            ?: throw SetNotFoundException(id)
    }

    fun getAllSets(): List<Set> {
        return setRepository.findAll()
    }

    fun getSetsByLiftId(liftId: Long): List<Set> {
        return setRepository.findByLiftId(liftId)
    }

    fun getSetsByWorkoutExerciseId(workoutExerciseId: Long): List<Set> {
        return setRepository.findByWorkoutExerciseId(workoutExerciseId)
    }

    fun updateSet(id: Long, set: Set): Set {
        val existing = setRepository.findById(id)
            ?: throw SetNotFoundException(id)
        
        require(set.liftId > 0) { "Lift ID must be valid" }
        require(set.workoutExerciseId > 0) { "WorkoutExercise ID must be valid" }
        require(set.reps > 0) { "Reps must be positive" }
        
        val updated = existing.copy(
            liftId = set.liftId,
            workoutExerciseId = set.workoutExerciseId,
            order = set.order,
            reps = set.reps,
            weightKg = set.weightKg,
            restTimeSeconds = set.restTimeSeconds,
            notes = set.notes
        )
        
        return setRepository.save(updated)
    }

    fun deleteSet(id: Long) {
        if (!setRepository.findById(id).let { it != null }) {
            throw SetNotFoundException(id)
        }
        setRepository.deleteById(id)
    }
}
