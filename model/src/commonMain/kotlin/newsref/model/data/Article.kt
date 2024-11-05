package newsref.model.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Article(
    val id: Long = 0,
    val sourceId: Long = 0,
    val headline: String = "",
    val alternativeHeadline: String? = null,
    val description: String? = null,
    val cannonUrl: String? = null,
    val section: String? = null,
    val keywords: List<String>? = null,
    val wordCount: Int? = null,
    val isFree: Boolean? = null,
    val language: String? = null,
    val commentCount: Int? = null,
    val modifiedAt: Instant? = null,
)