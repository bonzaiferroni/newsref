package newsref.db.tables

import newsref.db.core.*
import newsref.db.utils.*
import newsref.model.dto.*
import org.jetbrains.exposed.sql.*

object ChapterSourceDtoAspect : Aspect<ChapterSourceDtoAspect>(
    ChapterSourceTable.leftJoin(PageTable).leftJoin(HostTable).leftJoin(NewsArticleTable)
) {
    val chapterId = add(ChapterSourceTable.chapterId)
    val sourceId = add(ChapterSourceTable.sourceId)
    val title = add(PageTable.title)
    val score = add(PageTable.score)
    val url = add(PageTable.url)
    val thumbnail = add(PageTable.thumbnail)
    val imageUrl = add(PageTable.imageUrl)
    val cachedWordCount = add(PageTable.contentCount)
    val sourceType = add(ChapterSourceTable.type)
    val textDistance = add(ChapterSourceTable.textDistance)
    val relevance = add(ChapterSourceTable.relevance)
    val hostCore = add(HostTable.core)
    val hostName = add(HostTable.name)
    val hostLogo = add(HostTable.logo)
    val seenAt = add(PageTable.seenAt)
    val accessedAt = add(PageTable.accessedAt)
    val publishedAt = add(PageTable.publishedAt)
}

fun ResultRow.toChapterSourceDto() = ChapterSourceDto(
    chapterId = this[ChapterSourceDtoAspect.chapterId].value,
    sourceId = this[ChapterSourceDtoAspect.sourceId].value,
    title = this[ChapterSourceDtoAspect.title],
    score = this[ChapterSourceDtoAspect.score] ?: 0,
    url = this[ChapterSourceDtoAspect.url],
    thumbnail = this[ChapterSourceDtoAspect.thumbnail],
    imageUrl = this[ChapterSourceDtoAspect.imageUrl],
    cachedWordCount = this[ChapterSourceDtoAspect.cachedWordCount] ?: 0,
    sourceType = this[ChapterSourceDtoAspect.sourceType],
    textDistance = this[ChapterSourceDtoAspect.textDistance],
    relevance = this[ChapterSourceDtoAspect.relevance],
    hostCore = this[ChapterSourceDtoAspect.hostCore],
    hostName = this[ChapterSourceDtoAspect.hostName],
    hostLogo = this[ChapterSourceDtoAspect.hostLogo],
    seenAt = this[ChapterSourceDtoAspect.seenAt].toInstantUtc(),
    accessedAt = this[ChapterSourceDtoAspect.accessedAt]?.toInstantUtc(),
    publishedAt = this[ChapterSourceDtoAspect.publishedAt]?.toInstantUtc(),
)