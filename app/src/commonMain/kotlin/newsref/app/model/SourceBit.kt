package newsref.app.model

import kotlinx.serialization.Serializable
import newsref.model.dto.SourceBitDto

fun SourceBitDto.toSourceBit() = SourceBit(
    id = this.id,
    url = this.url,
    imageUrl = this.imageUrl,
    title = this.title,
    score = this.score,
)

@Serializable
data class SourceBit(
    val id: Long,
    val url: String,
    val imageUrl: String?,
    val title: String?,
    val score: Int
)