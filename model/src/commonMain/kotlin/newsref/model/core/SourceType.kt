package newsref.model.core

enum class SourceType {
    Reference,
    Article,
}

fun PageType.toSourceType() = when {
    this == PageType.NEWS_ARTICLE -> SourceType.Article
    else -> SourceType.Reference
}