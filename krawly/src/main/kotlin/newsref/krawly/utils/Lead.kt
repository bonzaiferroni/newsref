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
	if (isInvalidSuffix()) {
		println("isMaybeArticle: Url too long:\n>   $this")
		return false
	}
	return true
}

fun Url.isMaybeRelevant(): Boolean {
	if (length > MAX_URL_CHARS) {
		println("isMaybeRelevant: Url too long: $length")
		return false
	}
	return true
}