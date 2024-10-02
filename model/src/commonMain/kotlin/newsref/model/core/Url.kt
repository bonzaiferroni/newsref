package newsref.model.core

open class Url internal constructor(
	val rawUrl: String,
	val requiredParams: Set<String>?,
	disallowedPaths: Set<String>?,
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

	override fun toString() = checkedUrl ?: rawUrl
	override fun equals(other: Any?) = other is Url && this.toString() == other.toString()
	override fun hashCode(): Int {
		var result = rawUrl.hashCode()
		result = 31 * result + (checkedUrl?.hashCode() ?: 0)
		return result
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

fun String.parseUnchecked() = Url(this, null, null)

fun String.parseMaybeRelative(context: Url) = this.maybeCombine(context).parseUnchecked()

fun String.tryParseUnchecked(): Url? = tryParseUrl { this.parseUnchecked() }

fun String.tryParseMaybeRelative(context: Url): Url? =
	tryParseUrl { this.maybeCombine(context).parseUnchecked() }