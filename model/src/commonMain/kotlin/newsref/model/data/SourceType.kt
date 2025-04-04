package newsref.model.data

enum class SourceType {
    Reference,
    Article,
}

fun ContentType.toSourceType() = when {
    this == ContentType.NewsArticle -> SourceType.Article
    else -> SourceType.Reference
}