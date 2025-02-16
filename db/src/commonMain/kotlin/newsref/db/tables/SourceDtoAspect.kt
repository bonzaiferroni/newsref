package newsref.db.tables

import newsref.db.core.Aspect
import newsref.db.utils.toInstantUtc
import newsref.model.dto.SourceDto
import org.jetbrains.exposed.sql.ResultRow

object SourceDtoAspect: Aspect(SourceTable) {
    val id = add(SourceTable.id)
    val url = add(SourceTable.url)
    val title = add(SourceTable.title)
    val type = add(SourceTable.type)
    val score = add(SourceTable.score)
    val imageUrl = add(SourceTable.imageUrl)
    val thumbnail = add(SourceTable.thumbnail)
    val embed = add(SourceTable.embed)
    val wordCount = add(SourceTable.contentCount)
    val seenAt = add(SourceTable.seenAt)
    val publishedAt = add(SourceTable.publishedAt)
}

internal fun ResultRow.toSourceDto() = SourceDto(
    id = this[SourceDtoAspect.id].value,
    url = this[SourceDtoAspect.url],
    title = this[SourceDtoAspect.title],
    type = this[SourceDtoAspect.type],
    score = this[SourceDtoAspect.score],
    imageUrl = this[SourceDtoAspect.imageUrl],
    thumbnail = this[SourceDtoAspect.thumbnail],
    embed = this[SourceDtoAspect.embed],
    wordCount = this[SourceDtoAspect.wordCount],
    seenAt = this[SourceDtoAspect.seenAt].toInstantUtc(),
    publishedAt = this[SourceDtoAspect.publishedAt]?.toInstantUtc(),
)