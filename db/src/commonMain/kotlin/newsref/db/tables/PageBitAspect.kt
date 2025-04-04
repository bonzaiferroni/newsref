package newsref.db.tables

import newsref.db.core.Aspect
import newsref.db.utils.*
import newsref.model.core.*
import newsref.model.dto.*
import org.jetbrains.exposed.sql.ResultRow

object PageBitAspect : Aspect<PageBitAspect, PageBitDto>(
    PageTable.leftJoin(HostTable),
    ResultRow::toPageBitDto
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
    val newsType = add(PageTable.articleType)
    val newsSection = add(PageTable.section)
    val publishedAt = add(PageTable.publishedAt)
    val seenAt = add(PageTable.seenAt)
}

internal fun ResultRow.toPageBitDto() = PageBitDto(
    id = this[PageBitAspect.id].value,
    hostId = this[PageBitAspect.hostId].value,
    url = this[PageBitAspect.url],
    imageUrl = this[PageBitAspect.thumbnail] ?: this[PageBitAspect.imageUrl] ?: this[PageBitAspect.hostLogo],
    hostCore = this[PageBitAspect.hostCore],
    headline = this.getOrNull(PageBitAspect.headline) ?: this[PageBitAspect.title],
    score = this[PageBitAspect.score] ?: 0,
    feedPosition = this[PageBitAspect.feedPosition],
    contentType = this[PageBitAspect.type] ?: ContentType.Unknown,
    articleType = this.getOrNull(PageBitAspect.newsType) ?: ArticleType.Unknown,
    newsSection = this.getOrNull(PageBitAspect.newsSection) ?: NewsSection.Unknown,
    existedAt = (this[PageBitAspect.publishedAt] ?: this[PageBitAspect.seenAt]).toInstantUtc(),
)