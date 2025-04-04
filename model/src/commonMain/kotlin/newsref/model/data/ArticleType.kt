package newsref.model.data

enum class ArticleType(override val title: String): TitleEnum {
    Unknown("Unknown"),
    Report("Report"),
    Opinion("Opinion"),
    Analysis("Analysis"),
    Investigation("Investigation"),
}