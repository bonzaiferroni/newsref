package newsref.web.utils

fun String.toAnchorString(href: String, inNewTab: Boolean = true) =
	"<a href=\"${href}\" ${if (inNewTab) "target=\"_blank\"" else ""}>$this</a>"

fun String?.replaceWithAnchor(urlText: String, href: String) = (this ?: "...$urlText...")
	.replace(urlText, urlText.toAnchorString(href))