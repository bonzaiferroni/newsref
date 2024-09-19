package streetlight.web

import io.kvision.core.Container
import io.kvision.panel.FlexPanel
import io.kvision.routing.Routing
import io.kvision.state.observableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun String.getIdFromUrl(): Int? {
    return this.substringAfterLast("/").toIntOrNull()
}

val coScope = CoroutineScope(Dispatchers.Default)

fun Container.launchedEffect(
    block: suspend CoroutineScope.() -> Unit,
) {
    coScope.launch {
        try {
            block()
        } catch (e: Exception) {
            console.log(e)
        }
    }
}

fun <T> StateFlow<T>.subscribe(
    block: (T) -> Unit,
)  {
    this.observableState.subscribe(block)
}

fun String.addQueryParameter(url: String, key: String, value: String): String {
    val separator = if (url.contains("?")) "&" else "?"
    return "$url$separator$key=$value"
}

fun String.getQueryParameter(key: String): String? {
    val query = this.substringAfter("?")
    val pairs = query.split("&")
    for (pair in pairs) {
        if (!pair.contains('=')) {
            continue
        }
        val (k, v) = pair.split("=")
        if (k == key) {
            return v
        }
    }
    return null
}

fun String.getUrlFragment(): String {
    return this.substringAfter("#")
}

fun Routing.getCurrentRoute(): String? {
    return this.current?.firstOrNull()?.url
}

var FlexPanel.gap: Int
    get() = this.getStyle("gap")?.substringBefore("px")?.toIntOrNull() ?: 0
    set(value) {
        this.setStyle("gap", "${value}px")
    }

class EzState<M, T, V>(
    private val model: M,
    private val flow: MutableStateFlow<T>,
    private val getter: (T) -> V,
    private val setter: (T, V) -> T
): ReadWriteProperty<M, V> {
    override fun getValue(thisRef: M, property: KProperty<*>): V {
        return getter(flow.value)
    }

    override fun setValue(thisRef: M, property: KProperty<*>, value: V) {
        val newValue = setter(flow.value, value)
        flow.value = newValue
    }
}
