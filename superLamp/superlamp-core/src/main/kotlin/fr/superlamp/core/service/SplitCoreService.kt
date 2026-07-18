package fr.superlamp.core.service

import fr.superlamp.core.exception.SplitNotFoundException
import fr.superlamp.core.model.Split
import fr.superlamp.core.ports.SplitRepositoryPort

/**
 * Service métier pour la gestion des Splits
 */
class SplitCoreService(
    private val splitRepository: SplitRepositoryPort
) {
    fun createSplit(split: Split): Split {
        require(split.name.isNotBlank()) { "Split name cannot be empty" }
        return splitRepository.save(split)
    }

    fun getSplitById(id: Long): Split {
        return splitRepository.findById(id)
            ?: throw SplitNotFoundException(id)
    }

    fun getAllSplits(): List<Split> {
        return splitRepository.findAll()
    }

    fun updateSplit(id: Long, split: Split): Split {
        val existing = splitRepository.findById(id)
            ?: throw SplitNotFoundException(id)
        
        require(split.name.isNotBlank()) { "Split name cannot be empty" }
        
        val updated = existing.copy(
            name = split.name,
            description = split.description
        )
        
        return splitRepository.save(updated)
    }

    fun deleteSplit(id: Long) {
        if (!splitRepository.findById(id).let { it != null }) {
            throw SplitNotFoundException(id)
        }
        splitRepository.deleteById(id)
    }
}
