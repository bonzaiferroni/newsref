package newsref.web.ui.widgets

import io.kvision.core.Container
import newsref.web.ui.components.blockquote

fun Container.quote(content: String, rich: Boolean = false) {
	blockquote(
		className = "border-l-4 border-gray-400 pl-4 italic text-gray-300",
		content = content,
		rich = rich
	)
}