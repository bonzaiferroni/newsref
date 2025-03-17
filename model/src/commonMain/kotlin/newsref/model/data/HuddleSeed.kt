package newsref.model.data

import kotlinx.serialization.Serializable
import newsref.model.core.HuddleType

@Serializable
data class HuddleSeed(
    val chapterId: Long? = null,
    val pageId: Long? = null,
    val huddleId: Long? = null,
    val type: HuddleType,
    val value: String,
    val comment: String,
)