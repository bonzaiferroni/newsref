package newsref.db.tables

import newsref.db.core.Aspect
import newsref.db.utils.toInstantUtc
import newsref.model.dto.SourceDto
import org.jetbrains.exposed.sql.ResultRow

object SourceDtoAspect : Aspect<SourceDtoAspect, SourceDto>(
    PageTable.leftJoin(NewsArticleTable).leftJoin(ArticleTable),
    ResultRow::toSourceDto
) {
    val pageId = add(PageTable.id)
    val hostId = add(PageTable.hostId)
    val url = add(PageTable.url)
    val title = add(PageTable.title)
    val headline = add(ArticleTable.headline)
    val type = add(PageTable.type)
    val score = add(PageTable.score)
    val imageUrl = add(PageTable.imageUrl)
    val thumbnail = add(PageTable.thumbnail)
    val embed = add(PageTable.embed)
    val wordCount = add(ArticleTable.wordCount)
    val summary = add(NewsArticleTable.summary)
    val newsType = add(NewsArticleTable.articleType)
    val seenAt = add(PageTable.seenAt)
    val accessedAt = add(PageTable.accessedAt)
    val publishedAt = add(PageTable.publishedAt)
}

internal fun ResultRow.toSourceDto() = SourceDto(
    pageId = this[SourceDtoAspect.pageId].value,
    hostId = this[SourceDtoAspect.hostId].value,
    url = this[SourceDtoAspect.url],
    headline = this.getOrNull(SourceDtoAspect.headline) ?: this[SourceDtoAspect.title],
    type = this[SourceDtoAspect.type],
    score = this[SourceDtoAspect.score],
    imageUrl = this[SourceDtoAspect.imageUrl],
    thumbnail = this[SourceDtoAspect.thumbnail],
    embed = this[SourceDtoAspect.embed],
    wordCount = this[SourceDtoAspect.wordCount],
    summary = this[SourceDtoAspect.summary],
    articleType = this[SourceDtoAspect.newsType],
    seenAt = this[SourceDtoAspect.seenAt].toInstantUtc(),
    accessedAt = this[SourceDtoAspect.accessedAt]?.toInstantUtc(),
    publishedAt = this[SourceDtoAspect.publishedAt]?.toInstantUtc(),
)