package newsref.krawly.utils

import newsref.krawly.MAX_URL_CHARS
import newsref.model.core.Url

val NOT_ARTICLE_SUFFIX = setOf(
	"pdf",
	"zip"
)

fun Url.isInvalidSuffix(): Boolean {
	return path.split(".").lastOrNull()?.let { it in NOT_ARTICLE_SUFFIX } ?: false
}

fun Url.isMaybeArticle(): Boolean {
	return !isInvalidSuffix()
}

fun Url.isMaybeRelevant(): Boolean {
	return length <= MAX_URL_CHARS
}