package newsref.model.core

enum class PageType(
    val displayName: String
) {
    UNKNOWN("Unknown"),
    NEWS_ARTICLE("News Article"),
    ARTICLE("Article"),
    BLOG_POST("Blog Post"),
    SOCIAL_POST("Social Media Post"),
    VIDEO("Video"),
    IMAGE("Image"),
    PODCAST("Podcast"),
    WEBSITE("Website"),
    PROFILE("Profile"),
    PRODUCT("Product"),
    NEWS_FEED("News Feed");

    companion object {
        fun fromMeta(value: String) = fromMetaOrNull(value) ?: UNKNOWN

        fun fromMetaOrNull(value: String) = when (value) {
            "article" -> NEWS_ARTICLE
            // post ?
            "website" -> WEBSITE
            "profile" -> PROFILE
            else -> null
        }
    }
}

enum class ArticleType {
    UNKNOWN, NEWS, HELP, POLICY, JOURNAL
}

fun String.toSourceType(): PageType = PageType.fromMeta(this)