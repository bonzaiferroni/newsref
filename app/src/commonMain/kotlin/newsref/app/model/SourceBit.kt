package newsref.app.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import newsref.model.core.*
import newsref.model.dto.SourceBitDto

fun SourceBitDto.toSourceBit() = SourceBit(
    id = this.id,
    url = this.url,
    imageUrl = this.imageUrl,
    hostCore = this.hostCore,
    title = this.title,
    score = this.score,
    pageType = this.pageType,
    existedAt = this.existedAt,
)

@Serializable
data class SourceBit(
    val id: Long,
    val url: String,
    val imageUrl: String?,
    val hostCore: String,
    val title: String?,
    val score: Int,
    val pageType: PageType,
    val existedAt: Instant,
)