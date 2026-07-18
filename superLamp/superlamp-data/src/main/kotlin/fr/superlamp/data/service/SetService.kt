package fr.superlamp.data.service

import fr.superlamp.data.entity.Set
import fr.superlamp.data.repository.SetRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SetService(private val setRepository: SetRepository) {

    fun createSet(set: Set): Set {
        return setRepository.save(set)
    }

    fun getSetById(id: Long): Set? {
        return setRepository.findById(id).orElse(null)
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

    fun updateSet(id: Long, set: Set): Set? {
        return setRepository.findById(id).map { existing ->
            val updated = existing.copy(
                lift = set.lift,
                workoutExercise = set.workoutExercise,
                order = set.order,
                reps = set.reps,
                weightKg = set.weightKg,
                restTimeSeconds = set.restTimeSeconds,
                notes = set.notes
            )
            setRepository.save(updated)
        }.orElse(null)
    }

    fun deleteSet(id: Long) {
        setRepository.deleteById(id)
    }
}
