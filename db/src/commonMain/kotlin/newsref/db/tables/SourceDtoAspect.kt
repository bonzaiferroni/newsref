package newsref.db.tables

import newsref.db.core.Aspect
import newsref.db.utils.toInstantUtc
import newsref.model.dto.SourceDto
import org.jetbrains.exposed.sql.ResultRow

object SourceDtoAspect: Aspect<SourceDtoAspect, SourceDto>(PageTable, ResultRow::toSourceDto) {
    val id = add(PageTable.id)
    val url = add(PageTable.url)
    val title = add(PageTable.title)
    val type = add(PageTable.type)
    val score = add(PageTable.score)
    val imageUrl = add(PageTable.imageUrl)
    val thumbnail = add(PageTable.thumbnail)
    val embed = add(PageTable.embed)
    val wordCount = add(PageTable.contentCount)
    val seenAt = add(PageTable.seenAt)
    val publishedAt = add(PageTable.publishedAt)
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