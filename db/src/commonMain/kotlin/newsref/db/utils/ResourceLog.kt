package newsref.db.utils

import kotlinx.serialization.serializer
import newsref.db.globalConsole
import newsref.model.core.Url
import java.io.File

const val resourcePath = "../cache"

fun String.cacheResource(
    fileName: String,
    type: String,
    path: String = type,
    overwrite: Boolean = false,
): String {
    if (this.isBlank()) {
        globalConsole.logDebug("cacheResource", "found blank $type string from $fileName")
        return this
    }
    val file = File("$resourcePath/$path/$fileName.$type")
    file.parentFile?.mkdirs()
    if (!file.exists() || overwrite)
        file.writeText(this)
    return this
}

fun ByteArray.cacheResource(
    fileName: String,
    type: String,
    path: String = type,
    overwrite: Boolean = false,
): ByteArray {
    val file = File("$resourcePath/$path/$fileName.$type")
    file.parentFile?.mkdirs()
    if (!file.exists() || overwrite)
        file.writeBytes(this)
    return this
}

// General function to cache any serializable object
inline fun <reified T> T.cacheSerializable(
    fileName: String,
    path: String,
    overwrite: Boolean = false,
): T where T : Any {
    val file = File("$resourcePath/$path/$fileName.json")
    file.parentFile?.mkdirs()

    // Serialize object to JSON
    val jsonString = prettyPrintJson.encodeToString(serializer(), this)
    if (!file.exists() || overwrite)
        file.writeText(jsonString)

    return this
}

fun String.fileLog(
    path: String,
    fileName: String
) {
    val file = File("$resourcePath/$path/$fileName.log")
    file.parentFile?.mkdirs() // Create missing directories if they don't exist
    file.appendText("$this\n\n")
}