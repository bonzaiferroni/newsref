package newsref.dashboard

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.serialization.Serializable
import newsref.app.AppCache
import newsref.app.AppRoute
import newsref.app.CacheFile
import newsref.app.WatchWindow
import newsref.app.WindowSize

fun main() = application {
    val cacheFlow = CacheFile("appcache.json") { DashCache() }
    val cache by cacheFlow.collectAsState()

    val windowState = WatchWindow(cache.windowSize) {
        cacheFlow.value = cacheFlow.value.copy(windowSize = it)
    }

    Window(
        state = windowState,
        onCloseRequest = ::exitApplication,
        title = "Newsref Dashboard",
        undecorated = true,
    ) {
        Dash(cache.route, { cacheFlow.value = cache.copy(route = it )}, ::exitApplication)
    }
}

@Serializable
data class DashCache(
    val windowSize: WindowSize = WindowSize(600, 800),
    val route: DashRoute = HelloRoute("Luke"),
)