package newsref.dashboard

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.io.File

fun getAppData(): DashCache? {
    val file = File(fileName)
    if (!file.exists()) return null
    return Json.decodeFromString(serializer(), file.readText())
}

fun saveAppData(dashCache: DashCache) {
    File(fileName).writeText(Json.encodeToString(serializer(), dashCache))
}

private const val fileName = "appdata.json"

@Serializable
data class AppData(
    val width: Int = 600,
    val height: Int = 800,
    val route: DashRoute = HelloRoute("Luke"),
)