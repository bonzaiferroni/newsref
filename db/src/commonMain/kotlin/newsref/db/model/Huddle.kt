package newsref.db.model

import kotlinx.datetime.Instant
import newsref.model.data.HuddleStatus
import newsref.model.data.HuddleType
import newsref.model.data.HuddleOption

data class Huddle(
    val id: Long,
    val chapterId: Long,
    val pageId: Long,
    val initiatorId: Long,
    val huddleType: HuddleType,
    val guide: String,
    val options: List<HuddleOption>,
    val consensus: String?,
    val status: HuddleStatus,
    val startedAt: Instant,
    val finishedAt: Instant,
    val recordedAt: Instant?,
) {
}

data class HuddleResponse(
    val id: Long,
    val huddleId: Long,
    val userId: Long,
    val commentId: Long?,
    val response: String,
    val time: Instant,
)