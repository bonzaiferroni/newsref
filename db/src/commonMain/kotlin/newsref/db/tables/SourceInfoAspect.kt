package newsref.db.tables

import klutch.utils.toInstantUtc
import newsref.model.data.PageInfo
import org.jetbrains.exposed.sql.ResultRow

// sourceInfo
internal val pageInfoColumns = listOf(
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
    PageTable.headline,
    PageTable.description,
    PageTable.wordCount,
    PageTable.section,
)

val sourceInfoTables get () = PageTable.leftJoin(HostTable)
    .select(pageInfoColumns)

internal fun ResultRow.toPageInfo() = PageInfo(
    pageId = this[PageTable.id].value,
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
    headline = this.getOrNull(PageTable.headline),
    description = this.getOrNull(PageTable.description),
    wordCount = this.getOrNull(PageTable.wordCount),
    metaSection = this.getOrNull(PageTable.metaSection),
)