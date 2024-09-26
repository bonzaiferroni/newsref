package newsref.db.utils

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsArticle(
    val publisher: Publisher,
    val stnMeta: StnMeta,
    val headline: String,
    val url: String,
    val mainEntityOfPage: String,
    val dateModified: String,
    val datePublished: String,
    val identifier: Long,
    val thumbnailUrl: String,
    val image: String,
    val keywords: List<String>,
    val articleSection: String,
    val description: String,
    val author: List<Author>,
    val isAccessibleForFree: Boolean,
    val hasPart: HasPart
)

@Serializable
data class Publisher(
    val logo: Logo,
    val name: String
)

@Serializable
data class Logo(
    val url: String
)

@Serializable
data class StnMeta(
    val bmb: String,
    val campaign: String,
    val ch: String,
    val cid: String,
    val franchise: String,
    val permutive: List<String>,
    val tags: List<String>,
    val topics: List<String>
)

@Serializable
data class Author(
    val name: String,
    val url: String,
    val email: String,
    val image: String
)

@Serializable
data class HasPart(
    val cssSelector: String,
    val isAccessibleForFree: String
)