package newsref.model.data

data class ArticleSource(
    val sourceId: Long,
    val articleId: Long,
    val text: String,
    val context: String,
)