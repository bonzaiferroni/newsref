package newsref.web.ui.css

import io.kvision.core.ClassSetBuilder

data class Clasp(
	val name: String,
	val prefix: String? = null,
	val next: Clasp? = null
) {
	val className get() = prefix?.let { "$it:$name" } ?: name

	operator fun plus(other: Clasp): Clasp {
		return this.copy(next = if (next != null) next + other else other)
	}

	fun build(classSetBuilder: ClassSetBuilder) {
		classSetBuilder.add(className)
		next?.build(classSetBuilder)
	}

	private fun build(sb: StringBuilder) {
		if (next == null) {
			sb.append(className)
		} else {
			sb.append(className)
			sb.append(' ')
			next.build(sb)
		}
	}

	override fun toString(): String {
		val sb = StringBuilder()
		build(sb)
		return sb.toString()
	}
}

operator fun String.times(clasp: Clasp): Clasp {
	return clasp.copy(prefix = this, next = if (clasp.next != null) this * clasp.next else null)
}

fun clasp(clasp: Clasp?): String {
	val sb = StringBuilder()
	var next = clasp
	while (next != null) {
		if (sb.isNotEmpty()) sb.append(" ")
		sb.append(next.name)
		next = next.next
	}
	return sb.toString()
}

