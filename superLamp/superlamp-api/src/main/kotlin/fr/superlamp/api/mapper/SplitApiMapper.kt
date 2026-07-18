package fr.superlamp.api.mapper

import fr.superlamp.api.dto.SplitRequest
import fr.superlamp.api.dto.SplitResponse
import fr.superlamp.core.model.Split

/**
 * Mapper entre SplitRequest/SplitResponse et Split
 */
fun SplitRequest.toModel(): Split = Split(
    name = this.name,
    description = this.description
)

fun Split.toResponse(): SplitResponse = SplitResponse(
    id = this.id,
    name = this.name,
    description = this.description
)
