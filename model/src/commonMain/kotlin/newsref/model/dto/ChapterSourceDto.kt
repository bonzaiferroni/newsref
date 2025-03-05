package newsref.model.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import newsref.model.core.*

@Serializable
data class ChapterSourceDto(
    val chapterId: Long,
    val pageId: Long,
    val title: String?,
    val score: Int,
    val url: String,
    val thumbnail: String?,
    val imageUrl: String?,
    val cachedWordCount: Int,
    val sourceType: SourceType,
    val textDistance: Float?,
    val relevance: Relevance?,
    val summary: String?,
    val hostCore: String,
    val hostName: String?,
    val hostLogo: String?,
    val seenAt: Instant,
    val accessedAt: Instant?,
    val publishedAt: Instant?,
)

@Serializable
data class ChapterSourcePackDto(
    val source: ChapterSourceDto,
    val chapter: ChapterPackDto
)