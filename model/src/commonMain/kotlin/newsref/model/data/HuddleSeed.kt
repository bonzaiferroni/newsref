package newsref.model.data

import kotlinx.serialization.Serializable
import newsref.model.core.HuddleType

@Serializable
data class HuddleSeed(
    val key: HuddleKey,
    val value: String,
    val comment: String,
)

@Serializable
data class HuddleKey(
    val chapterId: Long? = null,
    val pageId: Long? = null,
    val huddleId: Long? = null,
    val type: HuddleType,
)

@Serializable
data class HuddlePrompt(
    val guide: String,
    val options: List<HuddleOption>,
    val cachedValue: String,
)

@Serializable
data class HuddleOption(
    val label: String,
    val value: String,
)