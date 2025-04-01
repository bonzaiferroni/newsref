package newsref.model.core

enum class ArticleType(override val title: String): TitleEnum {
    Unknown("Unknown"),
    Report("Report"),
    Opinion("Opinion"),
    Analysis("Analysis"),
    Investigation("Investigation"),
}