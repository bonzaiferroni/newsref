package newsref.db.model

import newsref.model.core.*

data class NewsArticle(
    val pageId: Long,
    // val placeId: Int = 0,
    val summary: String,
    val documentType: DocumentType,
    val section: NewsSection,
    val articleType: ArticleType,
)

// this is not a comprehensive list but intended to filter out misleading meta info
enum class DocumentType(override val title: String): TitleEnum {
    Unknown("Unknown"),
    NewsArticle("News Article"),
    ResearchArticle("Research Article"),
    PressRelease("Press Release"),
    TechSupport("Tech Support"),
    WebsitePolicy("Website Policy"),
    Missing("Missing Document"),
    Educational("Educational"),
    Profile("Personal Profile or Biography"),
    GeneralInformation("General Information"),
}