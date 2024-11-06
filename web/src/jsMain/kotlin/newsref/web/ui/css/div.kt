package newsref.web.ui.css

import io.kvision.core.Container
import io.kvision.html.TAG

open class Div(
	clasp: Clasp? = null,
	content: String? = null,
	rich: Boolean = false,
	className: String? = null,
	init: (Div.() -> Unit)? = null
) : ClaspTag(TAG.DIV, content, clasp, rich, className) {

	init {
		@Suppress("LeakingThis")
		init?.invoke(this)
	}
}

fun Container.div(
	clasp: Clasp? = null,
	content: String? = null,
	rich: Boolean = false,
	className: String? = null,
	init: (Div.() -> Unit)? = null
): Div {
	val div = Div(clasp, content, rich, className, init)
	this.add(div)
	return div
}

