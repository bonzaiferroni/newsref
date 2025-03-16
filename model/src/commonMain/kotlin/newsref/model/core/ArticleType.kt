package newsref.model.core

enum class ArticleType(override val title: String): TitleEnum {
    Unknown("Unknown"),
    Report("Report"),
    Perspective("Perspective"),
    Analysis("Analysis"),
    Investigation("Investigation"),
}