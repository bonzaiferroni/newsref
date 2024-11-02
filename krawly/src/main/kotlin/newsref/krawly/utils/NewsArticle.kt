package newsref.krawly.utils

import kotlinx.datetime.Instant
import newsref.db.utils.tryParse
import newsref.krawly.models.NewsArticle
import newsref.model.dto.PageAuthor

fun NewsArticle.readPublishedAt() = this.datePublished?.let { Instant.tryParse(it) }
fun NewsArticle.readModifiedAt() = this.dateModified?.let { Instant.tryParse(it) }
fun NewsArticle.readAuthor() = this.author?.mapNotNull { author ->
	if (author.name == null) return@mapNotNull null
	PageAuthor(name = author.name, url = author.url)
}
