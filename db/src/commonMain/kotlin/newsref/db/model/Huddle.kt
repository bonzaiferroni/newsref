package newsref.db.model

import kotlinx.datetime.Instant
import newsref.db.core.HuddleStatus
import newsref.model.core.HuddleType

data class Huddle(
    val id: Long,
    val chapterId: Long,
    val sourceId: Long,
    val initiatorId: Long,
    val huddleType: HuddleType,
    val guide: String,
    val options: List<String>,
    val consensus: Int?,
    val status: HuddleStatus,
    val startedAt: Instant,
    val finishedAt: Instant,
    val recordedAt: Instant?,
) {
}