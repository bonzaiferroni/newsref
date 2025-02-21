package newsref.db.model

data class ChapterSource(
    val id: Long = 0,
    val chapterId: Long = 0,
    val sourceId: Long,
    // val relevance: String? = null,
    // val contrast: String? = null,
    val type: NewsSourceType,
    val distance: Float?,
    val textDistance: Float?,
    val timeDistance: Float?,
    val linkDistance: Float?,
    val relevance: Relevance? = null,
)

enum class Relevance {
    Irrelevant,
    Relevant,
    Unsure,
}