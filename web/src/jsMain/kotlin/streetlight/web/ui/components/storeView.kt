package streetlight.web.ui.components

import io.kvision.core.Container
import io.kvision.html.div
import kotlinx.coroutines.flow.StateFlow
import streetlight.web.subscribe

fun <T> Container.renderStore(flow: StateFlow<T>, block: Container.(T) -> Unit) {
    val div = div()
    flow.subscribe {
        div.removeAll()
        div.block(it)
    }
}

fun <Store, Data> Container.renderStore(flow: StateFlow<Store>, map: (Store) -> Data, block: Container.(Store) -> Unit) {
    val div = col(className = "w-full")
    var value: Data? = null
    flow.subscribe {
        val newValue = map(it)
        if (value == newValue) return@subscribe
        value = newValue
        div.removeAll()
        div.block(it)
    }
}