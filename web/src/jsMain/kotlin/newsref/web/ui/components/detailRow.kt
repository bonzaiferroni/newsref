package newsref.web.ui.components

import io.kvision.core.Container
import io.kvision.html.div
import newsref.web.utils.md
import newsref.web.utils.tw

fun Container.detailRow(name: String, limitHeight: Boolean = false, block: Container.() -> Unit) {
	div(className = "flex flex-col pb-4 md:flex-row md:pb-0 w-full") {
		div(className = "text-muted text-center md:text-left md:basis-32 md:flex-shrink-0 md:flex-grow-0") {
			+name
		}
		val className = if (limitHeight) "overflow-auto h-64 w-full" else null
		div(className = className) {
			block()
		}
	}
}