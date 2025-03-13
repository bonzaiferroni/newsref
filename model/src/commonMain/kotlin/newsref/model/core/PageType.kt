package newsref.model.core

enum class PageType(
    val displayName: String
) {
    Unknown("Unknown"),
    NewsArticle("News Article"),
    Article("Article"),
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

enum class ArticleType {
    Unknown, News, Help, Policy, Journal
}

fun String.toSourceType(): PageType = PageType.fromMeta(this)