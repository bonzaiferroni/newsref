package newsref.db.tables

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.core.Aspect
import newsref.db.model.Source
import newsref.db.utils.*
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import kotlin.time.Duration

object SourceAspect : Aspect<SourceAspect>(PageTable) {
    val hostId = add(PageTable.hostId)
    val url = add(PageTable.url)
    val title = add(PageTable.title)
    val type = add(PageTable.type)
    val score = add(PageTable.score)
    val feedPosition = add(PageTable.feedPosition)
    val imageUrl = add(PageTable.imageUrl)
    val thumbnail = add(PageTable.thumbnail)
    val embed = add(PageTable.embed)
    val contentCount = add(PageTable.contentCount)
    val okResponse = add(PageTable.okResponse)
    val seenAt = add(PageTable.seenAt)
    val accessedAt = add(PageTable.accessedAt)
    val publishedAt = add(PageTable.publishedAt)
}

internal class SourceRow(id: EntityID<Long>) : LongEntity(id) {
    companion object : EntityClass<Long, SourceRow>(PageTable)

    var host by HostRow referencedOn PageTable.hostId
    var note by NoteRow optionalReferencedOn PageTable.noteId
    var contents by ContentRow via SourceContentTable

    var url by PageTable.url
    var title by PageTable.title
    var type by PageTable.type
    var score by PageTable.score
    var feedPosition by PageTable.feedPosition
    var imageUrl by PageTable.imageUrl
    var thumbnail by PageTable.thumbnail
    var embed by PageTable.embed
    var contentCount by PageTable.contentCount
    var okResponse by PageTable.okResponse
    var seenAt by PageTable.seenAt
    var accessedAt by PageTable.accessedAt
    var publishedAt by PageTable.publishedAt

    val links by LinkRow referrersOn LinkTable.sourceId
    val document by ArticleRow referrersOn ArticleTable.sourceId
    val scores by SourceScoreRow referrersOn SourceScoreTable.sourceId
    val authors by AuthorRow via SourceAuthorTable
}

internal fun SourceRow.toModel() = Source(
    id = this.id.value,
    hostId = this.host.id.value,
    noteId = this.note?.id?.value,
    url = this.url.toCheckedFromTrusted(),
    title = this.title,
    type = this.type,
    score = this.score,
    feedPosition = this.feedPosition,
    imageUrl = this.imageUrl,
    thumbnail = this.thumbnail,
    embed = this.embed,
    contentCount = this.contentCount,
    okResponse = this.okResponse,
    seenAt = this.seenAt.toInstantUtc(),
    accessedAt = this.accessedAt?.toInstantUtc(),
    publishedAt = this.publishedAt?.toInstantUtc(),
)

internal fun ResultRow.toSource() = Source(
    id = this[PageTable.id].value,
    hostId = this[PageTable.hostId].value,
    noteId = this.getOrNull(PageTable.noteId)?.value,
    url = this[PageTable.url].toCheckedFromTrusted(),
    title = this[PageTable.title],
    type = this[PageTable.type],
    score = this[PageTable.score],
    feedPosition = this[PageTable.feedPosition],
    imageUrl = this[PageTable.imageUrl],
    thumbnail = this[PageTable.thumbnail],
    embed = this[PageTable.embed],
    contentCount = this[PageTable.contentCount],
    okResponse = this[PageTable.okResponse],
    seenAt = this[PageTable.seenAt].toInstantUtc(),
    accessedAt = this[PageTable.accessedAt]?.toInstantUtc(),
    publishedAt = this[PageTable.publishedAt]?.toInstantUtc(),
)

internal fun SourceRow.fromModel(source: Source, hostRow: HostRow, isUpdate: Boolean) {
    host = hostRow
    url = source.url.toString()
    title = source.title
    score = source.score
    source.type?.let { type = it }
    imageUrl = source.imageUrl
    thumbnail = source.thumbnail
    embed = source.embed
    contentCount = source.contentCount
    if (!isUpdate)
        seenAt = source.seenAt.toLocalDateTimeUtc()
    accessedAt = source.accessedAt?.toLocalDateTimeUtc()
    publishedAt = source.publishedAt?.toLocalDateTimeUtc()
}

internal fun SourceRow.addContents(contentEntities: List<ContentRow>) {
    contents = SizedCollection(contentEntities)
}

internal fun PageTable.existedAfter(instant: Instant) = instant.toLocalDateTimeUtc().let {
    Op.build { (publishedAt.isNull() and seenAt.greater(it)) or publishedAt.greater(it) }
}

internal fun PageTable.existedSince(duration: Duration) = existedAfter(Clock.System.now() - duration)