package newsref.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChapterPackDto(
    val chapter: ChapterDto,
    val sourceBits: List<SourceBitDto>
)