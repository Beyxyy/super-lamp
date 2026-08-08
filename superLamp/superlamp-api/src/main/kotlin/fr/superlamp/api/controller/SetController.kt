package fr.superlamp.api.controller

import fr.superlamp.api.dto.SetRequest
import fr.superlamp.api.dto.SetResponse
import fr.superlamp.api.mapper.toModel
import fr.superlamp.api.mapper.toResponse
import fr.superlamp.core.service.SetCoreService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/sets")
class SetController(
    private val setCoreService: SetCoreService
) {

    @GetMapping
    fun getAllSets(): List<SetResponse> {
        return setCoreService.getAllSets().map { it.toResponse() }
    }

    @GetMapping("/{id}")
    fun getSetById(@PathVariable id: Long): SetResponse {
        return setCoreService.getSetById(id).toResponse()
    }

    @GetMapping("/lift/{liftId}")
    fun getSetsByLiftId(@PathVariable liftId: Long): List<SetResponse> {
        return setCoreService.getSetsByLiftId(liftId).map { it.toResponse() }
    }

    @GetMapping("/workoutExercise/{workoutExerciseId}")
    fun getSetsByWorkoutExerciseId(@PathVariable workoutExerciseId: Long): List<SetResponse> {
        return setCoreService.getSetsByWorkoutExerciseId(workoutExerciseId).map { it.toResponse() }
    }

    @PostMapping
    fun create(@RequestBody request: SetRequest): SetResponse {
        val set = setCoreService.createSet(request.toModel())
        return set.toResponse()
    }

    @PutMapping("/{id}")
    fun updateSet(
        @PathVariable id: Long,
        @RequestBody request: SetRequest
    ): SetResponse {
        val set = request.toModel().copy(id = id)
        return setCoreService.updateSet(id, set).toResponse()
    }

    @DeleteMapping("/{id}")
    fun deleteSet(@PathVariable id: Long) {
        setCoreService.deleteSet(id)
    }
}
