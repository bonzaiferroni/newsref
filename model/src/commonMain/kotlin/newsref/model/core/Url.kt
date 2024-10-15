package newsref.model.core

open class Url internal constructor(
	val rawHref: String,
	junkParams: Set<String>?,
	disallowedPaths: Set<String>?,
) {
	val core: String
	val scheme: String
	val domain: String
	val path: String
	val params: Map<String, String>
	val fragment: String?
	val isRobotAllowed: Boolean?
	val href: String

	val authority get() = "$scheme://$domain"
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
		domain = beforePath
		core = beforePath.removePrefix("www.")
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

	fun isMaybeSibling(other: Url): Boolean {
		val split1 = path.split('/')
		val root1 = split1.getOrNull(1) ?: return false
		val split2 = other.path.split('/')
		val root2 = split2.getOrNull(1) ?: return false
		// first segment same length or both have 1 segment
		return root1.length == root2.length || split1.size == 2 && split2.size == 2
	}
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