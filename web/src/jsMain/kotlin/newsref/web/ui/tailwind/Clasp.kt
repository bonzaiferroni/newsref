package newsref.web.ui.tailwind

import io.kvision.core.ClassSetBuilder

data class Clasp(
	val name: String,
	val next: Clasp? = null
) {
	operator fun plus(other: Clasp): Clasp {
		return other.copy(next = this)
	}

	fun build(classSetBuilder: ClassSetBuilder) {
		next?.build(classSetBuilder)
		classSetBuilder.add(name)
	}
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

