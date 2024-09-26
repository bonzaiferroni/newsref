package newsref.model.data

enum class SourceType(
    val displayName: String
) {
    UNKNOWN("Unknown"),
    ARTICLE("News Article"),
    JOURNAL_ARTICLE("Journal Article"),
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
        fun fromMeta(value: String) = when (value) {
            "article" -> ARTICLE
            // post ?
            "website" -> WEBSITE
            "profile" -> PROFILE
            else -> UNKNOWN
        }
    }
}