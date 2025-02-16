package newsref.db.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import newsref.model.dto.SourceCollection

@Serializable
data class SourceCache(
    val id: Int = 0,
    val sourceId: Long = 0,
    val score: Int,
    val createdAt: Instant,
    val json: SourceCollection,
)