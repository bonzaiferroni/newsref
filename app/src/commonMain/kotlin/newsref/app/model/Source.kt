package newsref.app.model

import kotlinx.datetime.Instant
import newsref.model.core.SourceType
import newsref.model.dto.SourceDto

data class Source(
    val id: Long = 0,
    val hostId: Int = 0,
    val noteId: Long? = null,
    val url: String,
    val title: String? = null,
    val type: SourceType? = null,
    val score: Int? = null,
    val feedPosition: Int? = null,
    val thumbnail: String? = null,
    val imageUrl: String? = null,
    val embed: String? = null,
    val contentCount: Int? = null,
    val okResponse: Boolean,
    val seenAt: Instant,
    val accessedAt: Instant? = null,
    val publishedAt: Instant? = null,
) {
    val existedAt get() = publishedAt ?: seenAt
}

fun SourceDto.toSource() = Source(
    id = this.id,
    hostId = this.hostId,
    noteId = this.noteId,
    url = this.url,
    title = this.title,
    type = this.type,
    score = this.score,
    feedPosition = this.feedPosition,
    thumbnail = this.thumbnail,
    imageUrl = this.imageUrl,
    embed = this.embed,
    contentCount = this.contentCount,
    okResponse = this.okResponse,
    seenAt = this.seenAt,
    accessedAt = this.accessedAt,
    publishedAt = this.publishedAt,
)