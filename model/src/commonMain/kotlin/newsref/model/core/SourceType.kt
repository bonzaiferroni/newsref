package newsref.model.core

enum class SourceType {
    Reference,
    Article,
}

fun PageType.toSourceType() = when {
    this == PageType.NewsArticle -> SourceType.Article
    else -> SourceType.Reference
}