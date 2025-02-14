package newsref.app

import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.serialization.Serializable

fun main() = application {
    val cacheFlow = CacheFile("appcache.json") { AppCache() }
    val cache by cacheFlow.collectAsState()

    val windowState = WatchWindow(cache.windowSize) {
        cacheFlow.value = cacheFlow.value.copy(windowSize = it)
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
    val windowSize: WindowSize = WindowSize(600, 800)
)