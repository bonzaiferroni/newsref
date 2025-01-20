package newsref.dashboard

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    // look for a file appdata.json and get initial width and height
    var appData = getAppData() ?: AppData()

    val windowState = rememberWindowState(
        width = appData.width.dp,
        height = appData.height.dp,
    )

    LaunchedEffect(windowState.size) {
        val size = windowState.size
        appData = appData.copy(width = size.width.value.toInt(), height = size.height.value.toInt())
        saveAppData(appData)
    }

    val cacheRoute = { route: ScreenRoute ->
        if (route != appData.route) {
            appData = appData.copy(route = route)
            saveAppData(appData)
        }
    }

    Window(
        state = windowState,
        onCloseRequest = ::exitApplication,
        title = "Newsref Dashboard",
        undecorated = true,
    ) {
        App(appData.route, cacheRoute, ::exitApplication)
    }
}
