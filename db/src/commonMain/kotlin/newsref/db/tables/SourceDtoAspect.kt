package newsref.db.tables

import newsref.db.core.Aspect
import newsref.db.utils.toInstantUtc
import newsref.model.dto.SourceDto
import org.jetbrains.exposed.sql.ResultRow

object SourceDtoAspect: Aspect(SourceTable) {
    val id = add(SourceTable.id)
    val hostId = add(SourceTable.hostId)
    val noteId = add(SourceTable.noteId)
    val url = add(SourceTable.url)
    val title = add(SourceTable.title)
    val type = add(SourceTable.type)
    val score = add(SourceTable.score)
    val feedPosition = add(SourceTable.feedPosition)
    val imageUrl = add(SourceTable.imageUrl)
    val thumbnail = add(SourceTable.thumbnail)
    val embed = add(SourceTable.embed)
    val contentCount = add(SourceTable.contentCount)
    val okResponse = add(SourceTable.okResponse)
    val seenAt = add(SourceTable.seenAt)
    val accessedAt = add(SourceTable.accessedAt)
    val publishedAt = add(SourceTable.publishedAt)
}

internal fun ResultRow.toSourceDto() = SourceDto(
    id = this[SourceDtoAspect.id].value,
    hostId = this[SourceDtoAspect.hostId].value,
    noteId = this.getOrNull(SourceDtoAspect.noteId)?.value,
    url = this[SourceDtoAspect.url],
    title = this[SourceDtoAspect.title],
    type = this[SourceDtoAspect.type],
    score = this[SourceDtoAspect.score],
    feedPosition = this[SourceDtoAspect.feedPosition],
    imageUrl = this[SourceDtoAspect.imageUrl],
    thumbnail = this[SourceDtoAspect.thumbnail],
    embed = this[SourceDtoAspect.embed],
    contentCount = this[SourceDtoAspect.contentCount],
    okResponse = this[SourceDtoAspect.okResponse],
    seenAt = this[SourceDtoAspect.seenAt].toInstantUtc(),
    accessedAt = this[SourceDtoAspect.accessedAt]?.toInstantUtc(),
    publishedAt = this[SourceDtoAspect.publishedAt]?.toInstantUtc(),
)