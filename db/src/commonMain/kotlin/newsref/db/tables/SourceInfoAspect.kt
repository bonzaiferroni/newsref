package newsref.db.tables

import newsref.db.utils.toInstantUtc
import newsref.model.dto.SourceInfo
import org.jetbrains.exposed.sql.ResultRow

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