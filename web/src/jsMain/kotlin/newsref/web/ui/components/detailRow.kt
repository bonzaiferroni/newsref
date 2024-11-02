package newsref.web.ui.components

import io.kvision.core.Container
import io.kvision.html.div
import newsref.web.utils.md
import newsref.web.utils.tw

fun Container.detailRow(name: String, block: Container.() -> Unit) {
	div(className = "flex flex-col pb-4 md:flex-row md:pb-0") {
		div(className = "text-gray-500 md:basis-32 md:flex-shrink-0 md:flex-grow-0") {
			+name
		}
		div {
			block()
		}
	}
}