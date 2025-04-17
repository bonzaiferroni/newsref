package newsref.db.tables

import klutch.db.Aspect
import klutch.utils.toInstantUtc
import newsref.model.data.ContentType
import newsref.model.data.NewsSection
import newsref.model.data.PageLite
import org.jetbrains.exposed.sql.ResultRow

object PageLiteAspect : Aspect<PageLiteAspect, PageLite>(
    PageTable.leftJoin(HostTable),
    ResultRow::toPageLite
) {
    val id = add(PageTable.id)
    val hostId = add(PageTable.hostId)
    val url = add(PageTable.url)
    val imageUrl = add(PageTable.imageUrl)
    val thumbnail = add(PageTable.thumbnail)
    val hostLogo = add(HostTable.logo)
    val hostCore = add(HostTable.core)
    val type = add(PageTable.contentType)
    val title = add(PageTable.title)
    val headline = add(PageTable.headline)
    val score = add(PageTable.score)
    val feedPosition = add(PageTable.feedPosition)
    val articleType = add(PageTable.articleType)
    val newsSection = add(PageTable.section)
    val publishedAt = add(PageTable.publishedAt)
    val seenAt = add(PageTable.seenAt)
}

internal fun ResultRow.toPageLite() = PageLite(
    id = this[PageLiteAspect.id].value,
    hostId = this[PageLiteAspect.hostId].value,
    url = this[PageLiteAspect.url],
    imageUrl = this[PageLiteAspect.thumbnail] ?: this[PageLiteAspect.imageUrl] ?: this[PageLiteAspect.hostLogo],
    hostCore = this[PageLiteAspect.hostCore],
    headline = this[PageLiteAspect.headline] ?: this[PageLiteAspect.title],
    score = this[PageLiteAspect.score] ?: 0,
    feedPosition = this[PageLiteAspect.feedPosition],
    contentType = this[PageLiteAspect.type] ?: ContentType.Unknown,
    articleType = this[PageLiteAspect.articleType],
    newsSection = this[PageLiteAspect.newsSection] ?: NewsSection.Unknown,
    existedAt = (this[PageLiteAspect.publishedAt] ?: this[PageLiteAspect.seenAt]).toInstantUtc(),
)