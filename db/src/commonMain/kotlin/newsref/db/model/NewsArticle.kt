package newsref.db.model

data class NewsArticle(
    val pageId: Long,
    // val placeId: Int = 0,
    val type: DocumentType,
    val summary: String,
    val category: NewsCategory,
)

// this is not a comprehensive list but intended to filter out misleading meta info

interface TitleEnum { val title: String }

enum class DocumentType(override val title: String): TitleEnum {
    Unknown("Unknown"),
    NewsArticle("News Article"),
    PressRelease("Press Release"),
    TechSupport("Tech Support"),
    WebsitePolicy("Website Policy"),
    Missing("Missing Document")
}

enum class NewsCategory(override val title: String): TitleEnum {
    Unknown("Unknown"),
    General("General News and Events"),
    Sports("Sports"),
    Government("Government and Policy"),
    Entertainment("Arts and Entertainment"),
    Weather("Weather"),
    International("International"),
    Technology("Internet and Technology"),
    Business("Business and Markets"),
    Science("Science and Space"),
    Celebrity("Celebrity and Influencer"),
    Editorial("Editorial and Opinion"),
}