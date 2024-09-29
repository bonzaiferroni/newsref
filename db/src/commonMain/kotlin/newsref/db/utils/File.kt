package newsref.db.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import newsref.db.serializers.globalJson
import newsref.model.utils.getApexDomain
import java.io.File

const val resourcePath = "cache"

fun String.cacheResource(url: String, type: String, path: String? = null): String {
    val apex = url.getApexDomain()
    if (this.isBlank()) {
        println("cacheResource: found blank $type string from $apex")
        return this
    }
    val file = File("$resourcePath/${path ?: type}/$apex.$type")
    file.parentFile?.mkdirs() // Create missing directories if they don't exist
    if (!file.exists()) {
        file.writeText(this)
    }
    return this
}

fun ByteArray.cacheResource(url: String, type: String, path: String? = null): ByteArray {
    val apex = url.getApexDomain()
    val file = File("$resourcePath/${path ?: type}/$apex.$type")
    file.parentFile?.mkdirs() // Create missing directories if they don't exist
    if (!file.exists()) {
        file.writeBytes(this)
    }
    return this
}

// General function to cache any serializable object
inline fun <reified T> T.cacheSerializable(url: String, type: String, path: String? = null): T where T : Any {
    val apex = url.getApexDomain()  // Assuming ye have this extension defined
    val file = File("$resourcePath/${path ?: type}/$apex.json")
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