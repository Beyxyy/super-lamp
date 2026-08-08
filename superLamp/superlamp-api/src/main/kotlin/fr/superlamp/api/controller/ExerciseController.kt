package fr.superlamp.api.controller

import fr.superlamp.api.dto.ExerciseRequest
import fr.superlamp.api.dto.ExerciseResponse
import fr.superlamp.api.mapper.toModel
import fr.superlamp.api.mapper.toResponse
import fr.superlamp.core.service.ExerciseCoreService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/exercises")
class ExerciseController(
    private val exerciseCoreService: ExerciseCoreService
) {

    @GetMapping
    fun getAllExercises(): List<ExerciseResponse> {
        return exerciseCoreService.getAllExercises().map { it.toResponse() }
    }

    @GetMapping("/{id}")
    fun getExerciseById(@PathVariable id: Long): ExerciseResponse {
        return exerciseCoreService.getExerciseById(id).toResponse()
    }

    @GetMapping("/muscleGroup/{muscleGroup}")
    fun getExercisesByMuscleGroup(@PathVariable muscleGroup: String): List<ExerciseResponse> {
        return exerciseCoreService.getExercisesByMuscleGroup(muscleGroup).map { it.toResponse() }
    }

    @PostMapping
    fun create(@RequestBody request: ExerciseRequest): ExerciseResponse {
        val exercise = exerciseCoreService.createExercise(request.toModel())
        return exercise.toResponse()
    }

    @PutMapping("/{id}")
    fun updateExercise(
        @PathVariable id: Long,
        @RequestBody request: ExerciseRequest
    ): ExerciseResponse {
        val exercise = request.toModel().copy(id = id)
        return exerciseCoreService.updateExercise(id, exercise).toResponse()
    }

    @DeleteMapping("/{id}")
    fun deleteExercise(@PathVariable id: Long) {
        exerciseCoreService.deleteExercise(id)
    }
}
