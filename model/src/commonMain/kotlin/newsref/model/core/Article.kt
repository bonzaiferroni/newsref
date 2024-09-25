package newsref.model.core

data class Article(
    override val id: Int,
    val title: String,
    val description: String,
    val url: String,
    val imageUrl: String,
    val publishedAt: String,
    val content: String,
    val sources: List<InfoSource>
): IdModel