package newsref.model.data

import kotlinx.serialization.Serializable

@Serializable
data class HuddleResponseSeed(
    val key: HuddleKey,
    val value: String,
    val comment: String,
    val createOption: Boolean,
)

@Serializable
data class HuddleKey(
    val chapterId: Long? = null,
    val pageId: Long? = null,
    val targetId: Long? = null,
    val type: HuddleType,
)

@Serializable
data class HuddlePrompt(
    val guide: String,
    val options: List<HuddleOption>,
    val cachedValue: String?,
    val activeId: Long?,
    val allowSuggestion: Boolean,
)

@Serializable
data class HuddleOption(
    val label: String?,
    val value: String,
)