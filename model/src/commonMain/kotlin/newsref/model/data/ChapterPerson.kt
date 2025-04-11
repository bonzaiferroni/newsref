package newsref.model.data

import kotlinx.serialization.Serializable

@Serializable
data class ChapterPerson(
    val personId: Int,
    val chapterId: Long,
    val name: String,
    val identifiers: Set<String>,
    val mentions: Int,
)