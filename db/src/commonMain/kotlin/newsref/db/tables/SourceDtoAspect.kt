package newsref.db.tables

import newsref.db.utils.toInstantUtc
import newsref.model.dto.SourceDto
import org.jetbrains.exposed.sql.ResultRow

internal val sourceDtoColumns = listOf(
    SourceTable.id,
    SourceTable.hostId,
    SourceTable.noteId,
    SourceTable.url,
    SourceTable.title,
    SourceTable.type,
    SourceTable.score,
    SourceTable.feedPosition,
    SourceTable.imageUrl,
    SourceTable.thumbnail,
    SourceTable.embed,
    SourceTable.contentCount,
    SourceTable.okResponse,
    SourceTable.seenAt,
    SourceTable.accessedAt,
    SourceTable.publishedAt,
)

internal fun ResultRow.toSourceDto() = SourceDto(
    id = this[SourceTable.id].value,
    hostId = this[SourceTable.hostId].value,
    noteId = this.getOrNull(SourceTable.noteId)?.value,
    url = this[SourceTable.url],
    title = this[SourceTable.title],
    type = this[SourceTable.type],
    score = this[SourceTable.score],
    feedPosition = this[SourceTable.feedPosition],
    imageUrl = this[SourceTable.imageUrl],
    thumbnail = this[SourceTable.thumbnail],
    embed = this[SourceTable.embed],
    contentCount = this[SourceTable.contentCount],
    okResponse = this[SourceTable.okResponse],
    seenAt = this[SourceTable.seenAt].toInstantUtc(),
    accessedAt = this[SourceTable.accessedAt]?.toInstantUtc(),
    publishedAt = this[SourceTable.publishedAt]?.toInstantUtc(),
)