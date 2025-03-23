package newsref.db.model

import kotlinx.datetime.Instant
import newsref.db.core.HuddleStatus
import newsref.model.core.HuddleType

data class Huddle(
    val id: Long,
    val chapterId: Long,
    val pageId: Long,
    val initiatorId: Long,
    val huddleType: HuddleType,
    val guide: String,
    val options: List<SerializedHuddleOption>,
    val consensus: Int?,
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
    val response: Int,
    val time: Instant,
)

data class SerializedHuddleOption(
    val label: String,
    val value: String,
)