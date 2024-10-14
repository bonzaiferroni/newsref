package newsref.db.utils

import kotlinx.serialization.serializer
import newsref.db.globalConsole
import newsref.model.core.Url
import java.io.File

const val resourcePath = "../cache"

fun String.cacheResource(
    url: Url,
    type: String,
    path: String = type,
    fileName: String = url.domain
): String {
    if (this.isBlank()) {
        globalConsole.logDebug("cacheResource", "found blank $type string from ${url.domain}")
        return this
    }
    val file = File("$resourcePath/$path/$fileName.$type")
    file.parentFile?.mkdirs() // Create missing directories if they don't exist
    file.writeText(this)
    return this
}

fun ByteArray.cacheResource(
    url: Url,
    type: String,
    path: String = type,
    fileName: String = url.domain
): ByteArray {
    val file = File("$resourcePath/$path/$fileName.$type")
    file.parentFile?.mkdirs() // Create missing directories if they don't exist
    file.writeBytes(this)
    return this
}

// General function to cache any serializable object
inline fun <reified T> T.cacheSerializable(
    url: Url,
    path: String,
    fileName: String = url.domain
): T where T : Any {
    val file = File("$resourcePath/$path/$fileName.json")
    file.parentFile?.mkdirs() // Create directories if they don't exist

    // Serialize object to JSON
    val jsonString = prettyPrintJson.encodeToString(serializer(), this)
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