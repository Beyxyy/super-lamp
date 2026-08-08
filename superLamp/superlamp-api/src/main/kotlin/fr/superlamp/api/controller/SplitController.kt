package fr.superlamp.api.controller

import fr.superlamp.api.dto.SplitRequest
import fr.superlamp.api.dto.SplitResponse
import fr.superlamp.api.mapper.toModel
import fr.superlamp.api.mapper.toResponse
import fr.superlamp.core.service.SplitCoreService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/splits")
class SplitController(
    private val splitCoreService: SplitCoreService
) {

    @GetMapping
    fun getAllSplits(): List<SplitResponse> {
        return splitCoreService.getAllSplits().map { it.toResponse() }
    }

    @GetMapping("/{id}")
    fun getSplitById(@PathVariable id: Long): SplitResponse {
        return splitCoreService.getSplitById(id).toResponse()
    }

    @PostMapping
    fun create(@RequestBody request: SplitRequest): SplitResponse {
        val split = splitCoreService.createSplit(request.toModel())
        return split.toResponse()
    }

    @PutMapping("/{id}")
    fun updateSplit(
        @PathVariable id: Long,
        @RequestBody request: SplitRequest
    ): SplitResponse {
        val split = request.toModel().copy(id = id)
        return splitCoreService.updateSplit(id, split).toResponse()
    }

    @DeleteMapping("/{id}")
    fun deleteSplit(@PathVariable id: Long) {
        splitCoreService.deleteSplit(id)
    }
}
