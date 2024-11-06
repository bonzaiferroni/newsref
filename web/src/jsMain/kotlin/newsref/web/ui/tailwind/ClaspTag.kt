package newsref.web.ui.tailwind

import io.kvision.core.ClassSetBuilder
import io.kvision.core.Container
import io.kvision.html.Align
import io.kvision.html.TAG
import io.kvision.html.Tag

open class ClaspTag (
	type: TAG,
	content: String? = null,
	var clasp: Clasp? = null,
	rich: Boolean = false,
	className: String? = null,
	attributes: Map<String, String>? = null,
	init: (Tag.() -> Unit)? = null
) : Tag(type, content, rich, null, className, attributes, init) {

	override fun buildClassSet(classSetBuilder: ClassSetBuilder) {
		clasp?.build(classSetBuilder)
		super.buildClassSet(classSetBuilder)
	}
}

fun Container.claspTag(
	type: TAG,
	content: String? = null,
	clasp: Clasp? = null,
	rich: Boolean = false,
	className: String? = null,
	attributes: Map<String, String>? = null,
	init: (Tag.() -> Unit)? = null
): ClaspTag {
	val tag = ClaspTag(type, content, clasp, rich, className, attributes, init)
	this.add(tag)
	return tag
}