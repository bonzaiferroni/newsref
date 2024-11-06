package newsref.web.ui.components

import io.kvision.core.Container
import newsref.web.ui.css.*

fun Container.iconLabel(icon: Clasp, clasp: Clasp? = null, block: Container.() -> Unit) {
	div(clasp = clasp ?: (row + items_center + justify_start)) {
		block()
		icon(icon)
	}
}

