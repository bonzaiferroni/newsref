package newsref.db.tables

import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import kotlinx.serialization.json.Json
import newsref.db.core.vector
import newsref.db.utils.toCheckedFromTrusted
import newsref.db.utils.toInstantUtc
import newsref.db.utils.toLocalDateTimeUtc
import newsref.model.core.SourceType
import newsref.model.data.FeedSource
import newsref.model.data.Source
import newsref.model.data.SourceScore
import newsref.model.dto.SourceCollection
import newsref.model.dto.SourceInfo
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

internal object SourceTable : LongIdTable("source") {
    val hostId = reference("host_id", HostTable, ReferenceOption.CASCADE).index()
    val noteId = reference("note_id", NoteTable, ReferenceOption.SET_NULL).nullable().index()
    val url = text("url").uniqueIndex()
    val title = text("title").nullable()
    val score = integer("score").nullable()
    val type = enumeration("source_type", SourceType::class).nullable()
    val imageUrl = text("image_url").nullable()
    val thumbnail = text("thumbnail").nullable()
    val embed = text("embed").nullable()
    val contentCount = integer("content_count").nullable()
    val embedding = vector("embedding", 1536).nullable()
    val seenAt = datetime("seen_at").index()
    val accessedAt = datetime("accessed_at").nullable()
    val publishedAt = datetime("published_at").nullable().index()
}

internal class SourceRow(id: EntityID<Long>) : LongEntity(id) {
    companion object : EntityClass<Long, SourceRow>(SourceTable)

    var host by HostRow referencedOn SourceTable.hostId
    var note by NoteRow optionalReferencedOn SourceTable.noteId
    var contents by ContentRow via SourceContentTable

    var url by SourceTable.url
    var title by SourceTable.title
    var score by SourceTable.score
    var type by SourceTable.type
    var imageUrl by SourceTable.imageUrl
    var thumbnail by SourceTable.thumbnail
    var embed by SourceTable.embed
    var contentCount by SourceTable.contentCount
    var embedding by SourceTable.embedding
    var seenAt by SourceTable.seenAt
    var accessedAt by SourceTable.accessedAt
    var publishedAt by SourceTable.publishedAt

    val links by LinkRow referrersOn LinkTable.sourceId
    val document by ArticleRow referrersOn ArticleTable.sourceId
    val scores by SourceScoreRow referrersOn SourceScoreTable.sourceId
    val authors by AuthorRow via SourceAuthorTable
}

internal fun SourceRow.toData() = Source(
    id = this.id.value,
    hostId = this.host.id.value,
    noteId = this.note?.id?.value,
    url = this.url.toCheckedFromTrusted(),
    title = this.title,
    score = this.score,
    type = this.type,
    imageUrl = this.imageUrl,
    thumbnail = this.thumbnail,
    embed = this.embed,
    contentCount = this.contentCount,
    seenAt = this.seenAt.toInstantUtc(),
    accessedAt = this.accessedAt?.toInstantUtc(),
    publishedAt = this.publishedAt?.toInstantUtc(),
)

internal fun ResultRow.toSource() = Source(
    id = this[SourceTable.id].value,
    hostId = this[SourceTable.hostId].value,
    noteId = this.getOrNull(SourceTable.noteId)?.value,
    url = this[SourceTable.url].toCheckedFromTrusted(),
    title = this[SourceTable.title],
    score = this[SourceTable.score],
    type = this[SourceTable.type],
    imageUrl = this[SourceTable.imageUrl],
    thumbnail = this[SourceTable.thumbnail],
    embed = this[SourceTable.embed],
    contentCount = this[SourceTable.contentCount],
    seenAt = this[SourceTable.seenAt].toInstantUtc(),
    accessedAt = this[SourceTable.accessedAt]?.toInstantUtc(),
    publishedAt = this[SourceTable.publishedAt]?.toInstantUtc(),
)

internal fun SourceRow.fromData(source: Source, hostRow: HostRow, isUpdate: Boolean) {
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

// source score
internal object SourceScoreTable : LongIdTable("source_score") {
    val sourceId = reference("source_id", SourceTable, ReferenceOption.CASCADE)
    val originId = reference("origin_id", SourceTable, ReferenceOption.CASCADE).nullable()
    val feedId = reference("feed_id", FeedTable, ReferenceOption.CASCADE).nullable()
    val score = integer("score")
    val scoredAt = datetime("scored_at")
}

internal class SourceScoreRow(id: EntityID<Long>) : LongEntity(id) {
    companion object : EntityClass<Long, SourceScoreRow>(SourceScoreTable)

    var origin by SourceRow optionalReferencedOn SourceScoreTable.originId
    var feed by FeedRow optionalReferencedOn SourceScoreTable.feedId

    var source by SourceRow referencedOn SourceScoreTable.sourceId
    var score by SourceScoreTable.score
    var scoredAt by SourceScoreTable.scoredAt
}

internal fun SourceScoreRow.toData() = SourceScore(
    sourceId = this.source.id.value,
    originId = this.origin?.id?.value,
    feedId = this.feed?.id?.value,
    score = this.score,
    scoredAt = this.scoredAt.toInstant(UtcOffset.ZERO)
)

internal fun ResultRow.toSourceScore() = SourceScore(
    sourceId = this[SourceScoreTable.sourceId].value,
    originId = this[SourceScoreTable.originId]?.value,
    feedId = this[SourceScoreTable.feedId]?.value,
    score = this[SourceScoreTable.score],
    scoredAt = this[SourceScoreTable.scoredAt].toInstantUtc(),
)

// feed source
internal object FeedSourceTable : IntIdTable("feed_source") {
    val sourceId = reference("source_id", SourceTable, ReferenceOption.CASCADE)
    val score = integer("score")
    val createdAt = datetime("created_at")
    val json = json<SourceCollection>("source", Json.Default)
}

internal class FeedSourceRow(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FeedSourceRow>(FeedSourceTable)
    var source by SourceRow referencedOn FeedSourceTable.sourceId
    var score by FeedSourceTable.score
    var createdAt by FeedSourceTable.createdAt
    var json by FeedSourceTable.json
}

internal fun FeedSourceRow.toData() = FeedSource(
    id = this.id.value,
    sourceId = this.source.id.value,
    score = this.score,
    createdAt = this.createdAt.toInstantUtc(),
    json = this.json,
)

internal fun FeedSourceRow.fromData(feedSource: FeedSource, sourceRow: SourceRow) {
    source = sourceRow
    score = feedSource.score
    createdAt = feedSource.createdAt.toLocalDateTimeUtc()
    json = feedSource.json
}

// sourceInfo
internal val sourceInfoColumns = listOf(
    SourceTable.id,
    SourceTable.url,
    SourceTable.title,
    SourceTable.score,
    SourceTable.imageUrl,
    SourceTable.thumbnail,
    SourceTable.seenAt,
    SourceTable.publishedAt,
    HostTable.core,
    HostTable.name,
    HostTable.logo,
    ArticleTable.headline,
    ArticleTable.description,
    ArticleTable.wordCount,
    ArticleTable.section,
    NoteTable.body
)

val sourceInfoTables get () = SourceTable.leftJoin(ArticleTable).leftJoin(HostTable).leftJoin(NoteTable)
    .select(sourceInfoColumns)

internal fun ResultRow.toSourceInfo() = SourceInfo(
    sourceId = this[SourceTable.id].value,
    url = this[SourceTable.url],
    pageTitle = this[SourceTable.title],
    score = this[SourceTable.score] ?: 0,
    image = this.getOrNull(SourceTable.imageUrl),
    thumbnail = this.getOrNull(SourceTable.thumbnail),
    seenAt = this[SourceTable.seenAt].toInstantUtc(),
    publishedAt = this.getOrNull(SourceTable.publishedAt)?.toInstantUtc(),
    hostCore = this[HostTable.core],
    hostName = this.getOrNull(HostTable.name),
    hostLogo = this.getOrNull(HostTable.logo),
    headline = this.getOrNull(ArticleTable.headline),
    description = this.getOrNull(ArticleTable.description),
    wordCount = this.getOrNull(ArticleTable.wordCount),
    section = this.getOrNull(ArticleTable.section),
    note = this.getOrNull(NoteTable.body),
)