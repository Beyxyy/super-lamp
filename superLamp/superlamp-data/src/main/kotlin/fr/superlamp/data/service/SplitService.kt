package fr.superlamp.data.service

import fr.superlamp.data.entity.Split
import fr.superlamp.data.repository.SplitRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SplitService(private val splitRepository: SplitRepository) {

    fun createSplit(split: Split): Split {
        return splitRepository.save(split)
    }

    fun getSplitById(id: Long): Split? {
        return splitRepository.findById(id).orElse(null)
    }

    fun getAllSplits(): List<Split> {
        return splitRepository.findAll()
    }

    fun updateSplit(id: Long, split: Split): Split? {
        return splitRepository.findById(id).map { existing ->
            val updated = existing.copy(
                name = split.name,
                description = split.description
            )
            splitRepository.save(updated)
        }.orElse(null)
    }

    fun deleteSplit(id: Long) {
        splitRepository.deleteById(id)
    }
}
