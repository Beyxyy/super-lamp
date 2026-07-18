package fr.superlamp.data.mapper

import fr.superlamp.core.model.Split as SplitModel
import fr.superlamp.data.entity.Split as SplitEntity

/**
 * Mapper entre SplitEntity (JPA) et Split (modèle métier)
 */
fun SplitEntity.toModel(): SplitModel = SplitModel(
    id = this.id,
    name = this.name,
    description = this.description
)

fun SplitModel.toEntity(): SplitEntity = SplitEntity(
    id = this.id,
    name = this.name,
    description = this.description
)
