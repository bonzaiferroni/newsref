package newsref.dashboard

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.io.File

fun main() = application {
    // look for a file appdata.json and get initial width and height
    val fileName = "appdata.json"
    val appData = File(fileName)
        .takeIf { it.exists() }
        ?.readText()
        ?.let { json -> Json.decodeFromString<AppData>(json) }

    val windowState = rememberWindowState(
        width = (appData?.width ?: 600).dp,
        height = (appData?.height ?: 800).dp,
    )

    LaunchedEffect(windowState.size) {
        val size = windowState.size
        val appData = AppData(size.width.value.toInt(), size.height.value.toInt())
        File(fileName).writeText(Json.encodeToString(serializer(), appData))
    }

    Window(
        state = windowState,
        onCloseRequest = ::exitApplication,
        title = "Newsref Dashboard",
        undecorated = true,
    ) {
        App(::exitApplication)
    }
}

@Serializable
class AppData(
    val width: Int?,
    val height: Int?,
)