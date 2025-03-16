package newsref.db.tables

import newsref.db.core.Aspect
import newsref.db.utils.*
import newsref.model.core.*
import newsref.model.dto.*
import org.jetbrains.exposed.sql.ResultRow

object SourceBitAspect : Aspect<SourceBitAspect, SourceBitDto>(
    PageTable.leftJoin(HostTable).leftJoin(ArticleTable).leftJoin(NewsArticleTable),
    ResultRow::toSourceBitDto
) {
    val id = add(PageTable.id)
    val hostId = add(PageTable.hostId)
    val url = add(PageTable.url)
    val imageUrl = add(PageTable.imageUrl)
    val thumbnail = add(PageTable.thumbnail)
    val hostLogo = add(HostTable.logo)
    val hostCore = add(HostTable.core)
    val type = add(PageTable.type)
    val title = add(PageTable.title)
    val headline = add(ArticleTable.headline)
    val score = add(PageTable.score)
    val feedPosition = add(PageTable.feedPosition)
    val newsType = add(NewsArticleTable.articleType)
    val newsSection = add(NewsArticleTable.section)
    val publishedAt = add(PageTable.publishedAt)
    val seenAt = add(PageTable.seenAt)
}

internal fun ResultRow.toSourceBitDto() = SourceBitDto(
    id = this[SourceBitAspect.id].value,
    hostId = this[SourceBitAspect.hostId].value,
    url = this[SourceBitAspect.url],
    imageUrl = this[SourceBitAspect.thumbnail] ?: this[SourceBitAspect.imageUrl] ?: this[SourceBitAspect.hostLogo],
    hostCore = this[SourceBitAspect.hostCore],
    headline = this.getOrNull(SourceBitAspect.headline) ?: this[SourceBitAspect.title],
    score = this[SourceBitAspect.score] ?: 0,
    feedPosition = this[SourceBitAspect.feedPosition],
    pageType = this[SourceBitAspect.type] ?: PageType.Unknown,
    articleType = this.getOrNull(SourceBitAspect.newsType) ?: ArticleType.Unknown,
    newsSection = this.getOrNull(SourceBitAspect.newsSection) ?: NewsSection.Unknown,
    existedAt = (this[SourceBitAspect.publishedAt] ?: this[SourceBitAspect.seenAt]).toInstantUtc(),
)