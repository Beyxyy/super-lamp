package fr.superlamp.api.controller

import fr.superlamp.api.dto.LiftRequest
import fr.superlamp.api.dto.LiftResponse
import fr.superlamp.api.mapper.toModel
import fr.superlamp.api.mapper.toResponse
import fr.superlamp.core.service.LiftCoreService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/lifts")
class LiftController(
    private val liftCoreService: LiftCoreService
) {

    @GetMapping
    fun getAllLifts(): List<LiftResponse> {
        return liftCoreService.getAllLifts().map { it.toResponse() }
    }

    @GetMapping("/{id}")
    fun getLiftById(@PathVariable id: Long): LiftResponse {
        return liftCoreService.getLiftById(id).toResponse()
    }

    @GetMapping("/user/{userId}")
    fun getLiftsByUserId(@PathVariable userId: Long): List<LiftResponse> {
        return liftCoreService.getLiftsByUserId(userId).map { it.toResponse() }
    }

    @GetMapping("/workout/{workoutId}")
    fun getLiftsByWorkoutId(@PathVariable workoutId: Long): List<LiftResponse> {
        return liftCoreService.getLiftsByWorkoutId(workoutId).map { it.toResponse() }
    }

    @PostMapping
    fun create(@RequestBody request: LiftRequest): LiftResponse {
        val lift = liftCoreService.createLift(request.toModel())
        return lift.toResponse()
    }

    @PutMapping("/{id}")
    fun updateLift(
        @PathVariable id: Long,
        @RequestBody request: LiftRequest
    ): LiftResponse {
        val lift = request.toModel().copy(id = id)
        return liftCoreService.updateLift(id, lift).toResponse()
    }

    @DeleteMapping("/{id}")
    fun deleteLift(@PathVariable id: Long) {
        liftCoreService.deleteLift(id)
    }
}
