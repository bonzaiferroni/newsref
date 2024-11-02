package newsref.web.ui.components

import io.kvision.core.Container
import io.kvision.html.Align
import io.kvision.html.TAG
import io.kvision.html.Tag
import io.kvision.html.tag

fun Container.blockquote(
	content: String? = null,
	rich: Boolean = false, align: Align? = null,
	className: String? = null,
	attributes: Map<String, String>? = null,
	init: (Tag.() -> Unit)? = null
): Tag {
	val tag = Tag(TAG.BLOCKQUOTE, content, rich, align, className, attributes, init)
	this.add(tag)
	return tag
}