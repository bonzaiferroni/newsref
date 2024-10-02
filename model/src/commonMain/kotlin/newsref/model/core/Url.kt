package newsref.model.core

open class Url internal constructor(
	val rawUrl: String,
	val requiredParams: Set<String>?,
	val disallowedPaths: Set<String>?,
) {
	val scheme: String
	val host: String
	val path: String
	val params: Map<String, String>
	val fragment: String?
	val isDisallowed: Boolean?
	val checkedUrl: String?
	val authority get() = "$scheme://$host"

	init {
		val (beforeFragment, afterFragment) = rawUrl.deconstruct("#")
		fragment = afterFragment
		val (beforeScheme, afterScheme) = beforeFragment.deconstruct("://")
		requireNotNull(afterScheme) { "Invalid Url: $rawUrl" }
		scheme = beforeScheme

		val (beforePath, afterPath) = afterScheme.deconstruct("/")
		if (beforePath.contains('@')) throw IllegalArgumentException("URL contains user info: $rawUrl")
		host = beforePath
		val rawPath = afterPath ?: "/"

		val (beforeParams, afterParams) = rawPath.deconstruct("?")
		var requiredParamPath = ""

		params = afterParams?.split("&")?.mapNotNull { param ->
			val (key, value) = param.deconstruct("=")
			if (value == null) return@mapNotNull null
			if (requiredParams != null && key !in requiredParams) return@mapNotNull null

			requiredParamPath += if (requiredParamPath.isEmpty()) "?" else "&"
			requiredParamPath += param
			Pair(key, value)
		}?.toMap() ?: emptyMap()
		path = beforeParams + requiredParamPath
		isDisallowed = disallowedPaths?.any { path.startsWith(it) }
		checkedUrl = if (requiredParams != null && isDisallowed != null && !isDisallowed)
			"$authority$path#$fragment"
		else null
	}

	companion object {
		fun parseUnchecked(url: String) = Url(url, null, null)

		fun parseMaybeRelative(url: String, context: Url) = parseUnchecked(url.maybeCombine(context))

		fun tryParseUnchecked(url: String): Url? =
			tryParseUrl { parseUnchecked(url) }

		fun tryParseMaybeRelative(url: String, context: Url): Url? =
			tryParseUrl { parseMaybeRelative(url, context) }
	}
}

internal fun tryParseUrl(block: () -> Url): Url? {
	return try {
		block()
	} catch (e: IllegalArgumentException) {
		null
	}
}

private fun String.deconstruct(delimiter: String): Pair<String, String?> =
	this.split(delimiter).let { Pair(it[0], it.getOrNull(1)) }

internal fun String.maybeCombine(context: Url) = if (this.contains("://")) {
	this
} else {
	"${context.authority}${if (this.startsWith("/")) "" else "/"}${this}"
}