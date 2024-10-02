package newsref.model.core

class SafeUrl(raw: String, val requiredParams: Set<String>) : MyUrl(raw) {
	override fun isRequired(key: String) = key in requiredParams
}

open class MyUrl(
	val raw: String
) {
	private val fragmentSplit by lazy { raw.deconstruct("#") }

	private val schemeSplit by lazy { fragmentSplit.first.deconstruct("://") }

	val scheme by lazy { schemeSplit.first }

	private val hostSplit by lazy {
		val afterScheme = schemeSplit.second
		requireNotNull(afterScheme) { "URL missing content after scheme: $raw" }
		afterScheme.deconstruct("/")
	}

	val host by lazy {
		val beforePath = hostSplit.first
		if (beforePath.contains('@')) throw IllegalArgumentException("URL contains user info: $raw")
		beforePath
	}

	val path by lazy { hostSplit.second?.let { "/$it" } ?: "/" }

	private val paramsSplit by lazy { path.deconstruct("?") }

	val pathBeforeParams by lazy { paramsSplit.first }

	val params: Map<String, String> by lazy {
		val afterDelimiter = paramsSplit.second
		if (afterDelimiter == null) {
			emptyMap() // return
		}
		else {
			val beforeFragment = afterDelimiter.split("#")[0]
			beforeFragment.split("&").mapNotNull { param ->
				val (key, value) = param.deconstruct("=")
				if (value == null || !isRequired(key)) return@mapNotNull null
				Pair(key, value)
			}.toMap() // return
		}
	}

	val fragment get() = fragmentSplit.second

	protected open fun isRequired(key: String) = true
}

private fun String.deconstruct(delimiter: String): Pair<String, String?> =
	this.split(delimiter).let { Pair(it[0], it.getOrNull(1)) }