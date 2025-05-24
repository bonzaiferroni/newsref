package newsref.krawly.agents

import kabinet.clients.GeminiClient
import kotlinx.coroutines.delay
import newsref.db.*
import newsref.db.model.Page
import java.io.File
import kotlin.time.Duration.Companion.seconds

private val console = globalConsole.getHandle("VectorClient")

class EmbeddingClient(
    private val client: GeminiClient,
) {

    suspend fun fetchVector(page: Page, model: String, content: String, maxAttempts: Int = 3): FloatArray? {
        val cachedVector = readFromCache(page.url.href, model, content.length)
        if (cachedVector != null) return cachedVector

        for (i in 0..maxAttempts) {
            val embedding = client.generateEmbeddings(content)
            if (embedding == null) {
                console.logWarning("No vector returned, resting for next attempt (attempts left: ${maxAttempts - i})")
                console.logWarning("content size: ${content.length}")
                delay(10.seconds)
                continue
            }
            saveToCache(page.url.href, model, content.length, embedding)
            return embedding
        }
        return null
    }
}

fun readFromCache(url: String, model: String, contentSize: Int): FloatArray? {
    val path = getPath(url, model, contentSize)
    val file = File(path)
    if (!file.exists()) return null
    console.log("retrieved cached vector")
    val bytes = file.readBytes()
    val floatBuffer = java.nio.ByteBuffer.wrap(bytes)
    return FloatArray(bytes.size / 4) { floatBuffer.getFloat() }
}

fun getPath(url: String, model: String, contentSize: Int) =
    "../cache/vectors/$model-${contentSize}-${url.filter { it.isLetterOrDigit() }.take(100)}.vec"

fun saveToCache(url: String, model: String, contentSize: Int, vector: FloatArray) {
    val path = getPath(url, model, contentSize)
    val file = File(path)
    file.parentFile?.mkdirs()
    val byteBuffer = java.nio.ByteBuffer.allocate(vector.size * 4)
    vector.forEach { byteBuffer.putFloat(it) }
    file.writeBytes(byteBuffer.array())
}