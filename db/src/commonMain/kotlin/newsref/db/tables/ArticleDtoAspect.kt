package newsref.db.tables

import newsref.db.core.Aspect
import newsref.db.utils.toInstantUtc
import newsref.model.dto.ArticleDto
import org.jetbrains.exposed.sql.ResultRow

object ArticleDtoAspect : Aspect<ArticleDtoAspect, ArticleDto>(
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

internal fun ResultRow.toSourceDto() = ArticleDto(
    pageId = this[ArticleDtoAspect.pageId].value,
    hostId = this[ArticleDtoAspect.hostId].value,
    url = this[ArticleDtoAspect.url],
    headline = this.getOrNull(ArticleDtoAspect.headline) ?: this[ArticleDtoAspect.title],
    type = this[ArticleDtoAspect.type],
    score = this[ArticleDtoAspect.score],
    imageUrl = this[ArticleDtoAspect.imageUrl],
    thumbnail = this[ArticleDtoAspect.thumbnail],
    embed = this[ArticleDtoAspect.embed],
    wordCount = this[ArticleDtoAspect.wordCount],
    summary = this[ArticleDtoAspect.summary],
    articleType = this[ArticleDtoAspect.newsType],
    seenAt = this[ArticleDtoAspect.seenAt].toInstantUtc(),
    accessedAt = this[ArticleDtoAspect.accessedAt]?.toInstantUtc(),
    publishedAt = this[ArticleDtoAspect.publishedAt]?.toInstantUtc(),
)