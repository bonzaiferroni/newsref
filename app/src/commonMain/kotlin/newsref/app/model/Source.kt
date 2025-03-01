package newsref.app.model

import kotlinx.datetime.Instant
import newsref.model.core.PageType
import newsref.model.dto.SourceDto

data class Source(
    val id: Long = 0,
    val url: String,
    val title: String? = null,
    val type: PageType? = null,
    val score: Int? = null,
    val thumbnail: String? = null,
    val imageUrl: String? = null,
    val embed: String? = null,
    val wordCount: Int? = null,
    val seenAt: Instant,
    val publishedAt: Instant? = null,
) {
    val existedAt get() = publishedAt ?: seenAt
}

fun SourceDto.toSource() = Source(
    id = this.id,
    url = this.url,
    title = this.title,
    type = this.type,
    score = this.score,
    thumbnail = this.thumbnail,
    imageUrl = this.imageUrl,
    embed = this.embed,
    wordCount = this.wordCount,
    seenAt = this.seenAt,
    publishedAt = this.publishedAt,
)

data class SourceImage(
    val id: Long,
    val url: String,
    val title: String,
    val score: Int
)