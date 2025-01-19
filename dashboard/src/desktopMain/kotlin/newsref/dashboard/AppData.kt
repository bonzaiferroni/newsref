package newsref.dashboard

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import newsref.dashboard.ui.screens.StartRoute
import java.io.File

fun getAppData(): AppData? {
    val file = File(fileName)
    if (!file.exists()) return null
    return Json.decodeFromString(serializer(), file.readText())
}

fun saveSize(size: androidx.compose.ui.unit.DpSize) {
    val appData = AppData(size.width.value.toInt(), size.height.value.toInt())
    File(fileName).writeText(Json.encodeToString(serializer(), appData))
}

fun cacheRoute(route: ScreenRoute) {

}

private const val fileName = "appdata.json"

@Serializable
class AppData(
    val width: Int?,
    val height: Int?,
    val route: ScreenRoute = StartRoute,
)