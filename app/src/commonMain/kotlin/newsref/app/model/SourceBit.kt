package newsref.app.model

import kotlinx.datetime.Instant
import kotlinx.serialization.*
import newsref.model.core.*
import newsref.model.dto.*

fun SourceBitDto.toSourceBit() = SourceBit(
    id = this.id,
    hostId = this.hostId,
    url = this.url,
    imageUrl = this.imageUrl,
    hostCore = this.hostCore,
    headline = this.headline,
    score = this.score,
    feedPosition = this.feedPosition,
    pageType = this.pageType,
    articleType = this.articleType,
    newsSection = this.newsSection,
    existedAt = this.existedAt,
)

@Serializable
data class SourceBit(
    val id: Long,
    val hostId: Int,
    val url: String,
    val imageUrl: String?,
    val hostCore: String,
    val headline: String?,
    val score: Int,
    val feedPosition: Int?,
    val pageType: PageType,
    val articleType: ArticleType,
    val newsSection: NewsSection,
    val existedAt: Instant,
)