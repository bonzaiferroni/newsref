package streetlight.web.utils

import io.kvision.core.Container
import io.kvision.html.Div
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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