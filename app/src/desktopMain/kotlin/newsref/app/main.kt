package newsref.app

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.serialization.Serializable

fun main() = application {
    val cacheFlow = FileCache("appcache.json") { AppCache() }
    val cache by cacheFlow.collectAsState()

    val windowState = rememberWindowState(
        width = cache.width.dp,
        height = cache.height.dp,
    )

    LaunchedEffect(windowState.size) {
        val size = windowState.size
        cacheFlow.value = cache.copy(
            width = size.width.value.toInt(),
            height = size.height.value.toInt()
        )
    }

    Window(
        state = windowState,
        onCloseRequest = ::exitApplication,
        title = "App",
    ) {
        App()
    }
}

@Serializable
data class AppCache(
    val width: Int = 600,
    val height: Int = 800,
)