package newsref.model.core

open class Url internal constructor(
	val rawHref: String,
	junkParams: Set<String>?,
	disallowedPaths: Set<String>?,
) {
	val scheme: String
	val host: String
	val path: String
	val params: Map<String, String>
	val fragment: String?
	val isRobotAllowed: Boolean?
	val href: String

	val authority get() = "$scheme://$host"
	val length get() = toString().length

	init {
		if (!rawHref.startsWith("http"))
			throw IllegalArgumentException("Url must begin with http: $rawHref")
		val (beforeFragment, afterFragment) = rawHref.deconstruct("#")
		fragment = afterFragment
		val (beforeScheme, afterScheme) = beforeFragment.deconstruct("://")
		requireNotNull(afterScheme) { "Invalid Url: $rawHref" }
		scheme = beforeScheme

		val (beforePath, afterPath) = afterScheme.deconstruct("/")
		if (beforePath.contains('@')) throw IllegalArgumentException("URL contains user info: $rawHref")
		host = beforePath
		val rawPath = afterPath?.let { "/$it" } ?: "/"

		val (beforeParams, afterParams) = rawPath.deconstruct("?")
		var requiredParamPath = ""

		params = afterParams?.split("&")?.mapNotNull { param ->
			val (key, value) = param.deconstruct("=")
			if (value == null) return@mapNotNull null
			if (junkParams != null && key in junkParams) return@mapNotNull null

			requiredParamPath += if (requiredParamPath.isEmpty()) "?" else "&"
			requiredParamPath += param
			Pair(key, value)
		}?.toMap() ?: emptyMap()
		path = beforeParams + requiredParamPath
		isRobotAllowed = disallowedPaths?.all { !path.startsWith(it) }
		href = if (junkParams != null) "$authority$path${fragment?.let { "#$it" } ?: ""}"
		else rawHref
	}

	override fun toString() = href
	override fun equals(other: Any?) = other is Url && this.toString() == other.toString()
	override fun hashCode(): Int {
		var result = rawHref.hashCode()
		result = 31 * result + href.hashCode()
		return result
	}

	fun isSibling(other: Url) = path.split('/')[0].equals(other.path.split('/')[0], ignoreCase = true)
}

internal fun <T: Url> tryParseUrl(block: () -> T): T? {
	return try { block() } catch (e: IllegalArgumentException) { null }
}

private fun String.deconstruct(delimiter: String): Pair<String, String?> =
	this.split(delimiter, limit = 2).let { Pair(it[0], it.getOrNull(1)) }

internal fun String.maybeCombine(context: Url) = if (this.contains("://")) {
	this
} else {
	"${context.authority}${if (this.startsWith("/")) "" else "/"}${this}"
}

fun String.toUrl() = Url(this, null, null)

fun String.toUrlWithContext(context: Url) = this.maybeCombine(context).toUrl()

fun String.toUrlOrNull(): Url? = tryParseUrl { this.toUrl() }

fun String.toUrlWithContextOrNull(context: Url): Url? =
	tryParseUrl { this.maybeCombine(context).toUrl() }