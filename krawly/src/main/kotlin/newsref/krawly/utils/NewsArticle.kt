package newsref.krawly.utils

import kotlinx.datetime.Instant
import klutch.utils.tryParse
import newsref.krawly.models.MetaNewsArticle
import newsref.model.data.CrawledAuthor

fun MetaNewsArticle.readPublishedAt() = this.datePublished?.let { Instant.tryParse(it) }
fun MetaNewsArticle.readModifiedAt() = this.dateModified?.let { Instant.tryParse(it) }
fun MetaNewsArticle.readAuthor() = this.author?.mapNotNull { author ->
	if (author.name == null) return@mapNotNull null
	CrawledAuthor(name = author.name, url = author.url)
}
