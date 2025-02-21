package newsref.db.tables

import newsref.db.utils.toInstantUtc
import newsref.model.dto.SourceInfo
import org.jetbrains.exposed.sql.ResultRow

// sourceInfo
internal val sourceInfoColumns = listOf(
    PageTable.id,
    PageTable.url,
    PageTable.title,
    PageTable.score,
    PageTable.imageUrl,
    PageTable.thumbnail,
    PageTable.seenAt,
    PageTable.publishedAt,
    HostTable.core,
    HostTable.name,
    HostTable.logo,
    ArticleTable.headline,
    ArticleTable.description,
    ArticleTable.wordCount,
    ArticleTable.section,
    NoteTable.body
)

val sourceInfoTables get () = PageTable.leftJoin(ArticleTable).leftJoin(HostTable).leftJoin(NoteTable)
    .select(sourceInfoColumns)

internal fun ResultRow.toSourceInfo() = SourceInfo(
    sourceId = this[PageTable.id].value,
    url = this[PageTable.url],
    pageTitle = this[PageTable.title],
    score = this[PageTable.score] ?: 0,
    image = this.getOrNull(PageTable.imageUrl),
    thumbnail = this.getOrNull(PageTable.thumbnail),
    seenAt = this[PageTable.seenAt].toInstantUtc(),
    publishedAt = this.getOrNull(PageTable.publishedAt)?.toInstantUtc(),
    hostCore = this[HostTable.core],
    hostName = this.getOrNull(HostTable.name),
    hostLogo = this.getOrNull(HostTable.logo),
    headline = this.getOrNull(ArticleTable.headline),
    description = this.getOrNull(ArticleTable.description),
    wordCount = this.getOrNull(ArticleTable.wordCount),
    section = this.getOrNull(ArticleTable.section),
    note = this.getOrNull(NoteTable.body),
)