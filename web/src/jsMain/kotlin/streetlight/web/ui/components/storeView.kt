package streetlight.web.ui.components

import io.kvision.core.Container
import io.kvision.html.div
import kotlinx.coroutines.flow.StateFlow
import streetlight.web.subscribe

fun <T> Container.storeView(flow: StateFlow<T>, block: Container.(T) -> Unit) {
    val div = div()
    flow.subscribe {
        div.removeAll()
        div.block(it)
    }
}