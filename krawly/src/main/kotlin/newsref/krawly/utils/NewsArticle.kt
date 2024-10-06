package newsref.krawly.utils

import kotlinx.datetime.Instant
import newsref.db.utils.tryParse
import newsref.krawly.models.NewsArticle

fun NewsArticle.readPublishedAt() = this.datePublished?.let { Instant.tryParse(it) }
fun NewsArticle.readModifiedAt() = this.dateModified?.let { Instant.tryParse(it) }
fun NewsArticle.readAuthor() = this.author?.firstOrNull()?.name
