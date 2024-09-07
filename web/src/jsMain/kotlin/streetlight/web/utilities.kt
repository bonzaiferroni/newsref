package streetlight.web

import io.kvision.core.Container
import io.kvision.core.CssSize
import io.kvision.core.UNIT
import io.kvision.routing.Routing
import io.kvision.state.ObservableState
import io.kvision.state.ObservableValue
import io.kvision.state.observableState
import io.kvision.window.Window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

