package newsref.model.dto

import kotlinx.datetime.Instant
import newsref.model.core.HuddleStatus
import newsref.model.core.HuddleType
import newsref.model.data.HuddleOption

data class HuddleContentDto(
    val id: Long,
    val initiatorId: Long,
    val huddleType: HuddleType,
    val consensus: String?,
    val status: HuddleStatus,
    val startedAt: Instant,
    val finishedAt: Instant,
    val recordedAt: Instant?,
)

data class HuddleResponseDto(
    val userId: Long,
    val response: String,
    val comment: String,
    val time: Instant,
)