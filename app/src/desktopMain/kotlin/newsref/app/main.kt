package newsref.app

import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.serialization.Serializable

fun main() {
    val dataStore = createDataStore { dataStoreFileName }

    application {
        val cacheFlow = CacheFile("appcache.json") { AppCache() }
        val cache by cacheFlow.collectAsState()

        val windowState = WatchWindow(cache.windowSize) {
            cacheFlow.value = cacheFlow.value.copy(windowSize = it)
        }

        Window(
            state = windowState,
            onCloseRequest = ::exitApplication,
            title = "App",
            undecorated = true,
        ) {
            App(cache.route, dataStore, { cacheFlow.value = cache.copy(route = it as AppRoute )}, ::exitApplication)
        }
    }
}

@Serializable
data class AppCache(
    val windowSize: WindowSize = WindowSize(600, 800),
    val route: AppRoute = StartRoute
)