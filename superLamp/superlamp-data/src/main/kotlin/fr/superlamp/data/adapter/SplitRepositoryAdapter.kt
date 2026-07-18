package fr.superlamp.data.adapter

import fr.superlamp.core.model.Split as SplitModel
import fr.superlamp.core.ports.SplitRepositoryPort
import fr.superlamp.data.mapper.toEntity
import fr.superlamp.data.mapper.toModel
import fr.superlamp.data.repository.SplitRepository
import org.springframework.stereotype.Component

/**
 * Adapter pour SplitRepositoryPort
 */
@Component
class SplitRepositoryAdapter(
    private val splitRepository: SplitRepository
) : SplitRepositoryPort {
    
    override fun save(split: SplitModel): SplitModel {
        val entity = split.toEntity()
        val savedEntity = splitRepository.save(entity)
        return savedEntity.toModel()
    }

    override fun findById(id: Long): SplitModel? {
        return splitRepository.findById(id).orElse(null)?.toModel()
    }

    override fun findAll(): List<SplitModel> {
        return splitRepository.findAll().map { it.toModel() }
    }

    override fun deleteById(id: Long) {
        splitRepository.deleteById(id)
    }
}
