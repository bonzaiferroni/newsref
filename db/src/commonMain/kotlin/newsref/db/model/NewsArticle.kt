package newsref.db.model

data class NewsArticle(
    val pageId: Long,
    // val placeId: Int = 0,
    val summary: String,
    val documentType: DocumentType,
    val category: NewsCategory,
    val newsType: NewsType?,
)

interface TitleEnum { val title: String }

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

enum class NewsType(override val title: String): TitleEnum {
    Report("Report"),
    Opinion("Opinion"),
    Analysis("Analysis"),
    Investigation("Investigation"),
}