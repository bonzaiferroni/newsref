package newsref.krawly.models

import kotlinx.serialization.Serializable
import newsref.krawly.serializers.*

@Serializable
data class NewsArticle(
    val headline: String? = null,
    val dateline: String? = null,
    val datePublished: String? = null,
    val dateModified: String? = null,
    @Serializable(SingleOrArraySerializer::class)
    val author: List<NewsAuthor>? = null,
    val publisher: Publisher? = null,
    val articleBody: String? = null,
    @Serializable(SingleOrArraySerializer::class)
    val articleSection: List<String>? = null,
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
data class NewsAuthor(
    val name: String? = null,
    val url: String? = null,
    val email: String? = null,
    val sameAs: String? = null,
    // @Serializable(ImageListSerializer::class)
    val image: Image? = null,
)

@Serializable
data class Publisher(
    val name: String? = null,
    // @Serializable(LogoListSerializer::class)
    val logo: Image? = null
)

@Serializable
data class Image(
    val width: Int? = null,
    val height: Int? = null,
    val url: String? = null,
    val caption: String? = null,
    val creditText: String? = null,
)

@Serializable
data class QuantitativeValue(
    val unitCode: String? = null,
    val value: Int? = null,
)