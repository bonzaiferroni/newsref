package newsref.db.utils

import com.eygraber.uri.Uri
import com.eygraber.uri.Url
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import newsref.db.serializers.globalJson
import java.io.File

const val resourcePath = "cache"

fun String.cacheResource(url: Uri, type: String, path: String? = null): String {
    if (this.isBlank()) {
        println("cacheResource: found blank $type string from ${url.host}")
        return this
    }
    val file = File("$resourcePath/${path ?: type}/${url.host}.$type")
    file.parentFile?.mkdirs() // Create missing directories if they don't exist
    if (!file.exists()) {
        file.writeText(this)
    }
    return this
}

fun ByteArray.cacheResource(url: Uri, type: String, path: String? = null): ByteArray {
    val file = File("$resourcePath/${path ?: type}/${url.host}.$type")
    file.parentFile?.mkdirs() // Create missing directories if they don't exist
    if (!file.exists()) {
        file.writeBytes(this)
    }
    return this
}

// General function to cache any serializable object
inline fun <reified T> T.cacheSerializable(url: Uri, type: String, path: String? = null): T where T : Any {
    val file = File("$resourcePath/${path ?: type}/${url.host}.json")
    file.parentFile?.mkdirs() // Create directories if they don't exist

    // Serialize object to JSON
    val jsonString = globalJson.encodeToString(serializer(), this)

    if (!file.exists()) {
        file.writeText(jsonString)
        println("Saved $type resource to $file")
    } else {
        println("$type resource already cached at $file")
    }

    return this
}