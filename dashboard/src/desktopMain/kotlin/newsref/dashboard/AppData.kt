package newsref.dashboard

import androidx.compose.ui.unit.DpSize
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.io.File

fun getAppData(): AppData? {
    val file = File(fileName)
    if (!file.exists()) return null
    return Json.decodeFromString(serializer(), file.readText())
}

fun saveAppData(appData: AppData) {
    File(fileName).writeText(Json.encodeToString(serializer(), appData))
}

private const val fileName = "appdata.json"

@Serializable
data class AppData(
    val width: Int = 600,
    val height: Int = 800,
    val route: ScreenRoute = StartRoute(),
)