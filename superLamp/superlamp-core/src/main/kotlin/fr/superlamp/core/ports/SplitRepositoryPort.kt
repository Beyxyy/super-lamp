package fr.superlamp.core.ports

import fr.superlamp.core.model.Split

/**
 * Port (interface) pour le repository Split
 */
interface SplitRepositoryPort {
    fun save(split: Split): Split
    fun findById(id: Long): Split?
    fun findAll(): List<Split>
    fun deleteById(id: Long)
}
