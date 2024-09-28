package newsref.db.models

import kotlinx.serialization.Serializable
import newsref.db.utils.ImageListSerializer
import newsref.db.utils.KeywordListSerializer
import newsref.db.utils.QuantitativeValueSerializer
import newsref.db.utils.SingleOrArraySerializer

@Serializable
data class NewsArticle(
    val dateline: String? = null,
    val headline: String? = null,
    val datePublished: String? = null,
    val dateModified: String? = null,
    @Serializable(SingleOrArraySerializer::class)
    val author: List<Author>? = null,
    val publisher: Publisher? = null,
    val articleBody: String? = null,
    val articleSection: String? = null,
    val wordCount: Int? = null,
    @Serializable(KeywordListSerializer::class)
    val keywords: List<String>? = null,
    val abstract: String? = null,
    val alternativeHeadline: String? = null,
    val description: String? = null,
    val url: String? = null,
    val isAccessibleForFree: Boolean? = null,
    val text: String? = null,
    val thumbnailUrl: String? = null,
    @Serializable(ImageListSerializer::class)
    val image: List<Image>? = null,
    val inLanguage: String? = null,
    val commentCount: Int? = null,
)

@Serializable
data class Author(
    val name: String? = null,
    val url: String? = null,
    val email: String? = null,
    val sameAs: String? = null,
    @Serializable(ImageListSerializer::class)
    val image: List<Image>? = null,
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
    @Serializable(QuantitativeValueSerializer::class)
    val width: QuantitativeValue? = null,
    @Serializable(QuantitativeValueSerializer::class)
    val height: QuantitativeValue? = null,
    val url: String? = null,
    val caption: String? = null,
    val creditText: String? = null,
)

@Serializable
data class QuantitativeValue(
    val unitCode: String? = null,
    val value: Int? = null,
)