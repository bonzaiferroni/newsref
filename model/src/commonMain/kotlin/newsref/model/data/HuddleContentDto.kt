package newsref.model.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class HuddleContentDto(
    val huddleId: Long,
    val huddleType: HuddleType,
    val consensus: String?,
    val status: HuddleStatus,
    val startedAt: Instant,
    val finishedAt: Instant,
    val recordedAt: Instant?,
)

@Serializable
data class HuddleResponseDto(
    val huddleId: Long,
    val responseId: Long,
    val username: String,
    val response: String,
    val comment: String?,
    val time: Instant,
)