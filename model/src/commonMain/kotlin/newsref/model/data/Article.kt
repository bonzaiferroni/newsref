package newsref.model.data

import kotlinx.datetime.Instant
import newsref.model.core.IdModel

data class Article(
    val id: Long,
    val title: String,
    val description: String,
    val url: String,
    val imageUrl: String,
    val content: String,
    val publishedAt: Instant,
    val updatedAt: Instant,
    val accessedAt: Instant,
)