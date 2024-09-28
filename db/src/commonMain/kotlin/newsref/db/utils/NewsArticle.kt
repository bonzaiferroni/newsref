package newsref.db.utils

import kotlinx.serialization.Serializable

@Serializable
data class NewsArticle(
    val dateline: String? = null,
    val headline: String? = null,
    val datePublished: String? = null,
    val dateModified: String? = null,
    @Serializable(with = SingleOrArraySerializer::class)
    val author: List<Author>? = null,
    val publisher: Publisher? = null,
    val articleBody: String? = null,
    val articleSection: String? = null,
    val wordCount: Int? = null,
    val keywords: String? = null,
    val abstract: String? = null,
    val alternativeHeadline: String? = null,
    val description: String? = null,
    val url: String? = null,
    val isAccessibleForFree: Boolean? = null,
    val text: String? = null,
    val thumbnailUrl: String? = null,
    @Serializable(with = SingleOrArraySerializer::class)
    val image: List<Image>? = null,
)

@Serializable
data class Author(
    val name: String? = null,
    val email: String? = null,
    val sameAs: String? = null,
)

@Serializable
data class Publisher(
    val name: String? = null,
    val logo: Logo? = null
)

@Serializable
data class Logo(
    val url: String? = null
)

@Serializable
data class Image(
    val width: QuantitativeValue,
    val height: QuantitativeValue,
    val url: String
)

@Serializable
data class QuantitativeValue(
    val unitCode: String,
    val value: Int
)