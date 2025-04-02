package newsref.model.core

enum class SourceType {
    Reference,
    Article,
}

fun ContentType.toSourceType() = when {
    this == ContentType.NewsArticle -> SourceType.Article
    else -> SourceType.Reference
}