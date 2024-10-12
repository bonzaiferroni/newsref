package newsref.krawly.utils

import newsref.krawly.MAX_URL_CHARS
import newsref.model.core.Url

val NOT_ARTICLE_SUFFIX = setOf(
	"pdf",
	"mp3",
	"zip"
)

fun Url.isFile(): Boolean {
	return path.split(".").lastOrNull()?.let { it in NOT_ARTICLE_SUFFIX } ?: false
}

// call this to filter ads and self-promotion
fun Url.isLikelyAd(): Boolean {
	return length > MAX_URL_CHARS
}

fun Url.isNotWebLink() = this.toString().contains("mailto:")