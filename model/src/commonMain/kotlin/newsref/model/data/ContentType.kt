package newsref.model.data

enum class ContentType(
    val displayName: String
) {
    Unknown("Unknown"),
    NewsArticle("News Article"),
    MiscArticle("Miscellaneous Article"),
    BlogPost("Blog Post"),
    SocialPost("Social Media Post"),
    Video("Video"),
    Image("Image"),
    Podcast("Podcast"),
    Website("Website"),
    Profile("Profile"),
    Product("Product"),
    NewsFeed("News Feed");

    companion object {
        fun fromMeta(value: String) = fromMetaOrNull(value) ?: Unknown

        fun fromMetaOrNull(value: String) = when (value) {
            "article" -> NewsArticle
            // post ?
            "website" -> Website
            "profile" -> Profile
            else -> null
        }
    }
}

enum class ArticleCategory {
    Unknown, News, Help, Policy, Journal
}

fun String.toSourceType(): ContentType = ContentType.fromMeta(this)