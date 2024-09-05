package streetlight.web

import io.kvision.core.Container
import io.kvision.core.CssSize
import io.kvision.core.UNIT
import io.kvision.state.ObservableState
import io.kvision.state.ObservableValue
import io.kvision.state.observableState
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
