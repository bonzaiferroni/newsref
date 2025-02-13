package newsref.krawly.agents

import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.delay
import newsref.db.core.*
import newsref.db.*
import newsref.krawly.clients.*
import newsref.model.data.*
import java.io.File
import kotlin.time.Duration.Companion.seconds

private val console = globalConsole.getHandle("VectorClient")

class VectorClient(
    private val env: Environment
) {

    suspend fun fetchVector(source: Source, model: String, content: String, maxAttempts: Int = 3): FloatArray? {
        val cachedVector = readFromCache(source.url.href, model, content.length)
        if (cachedVector != null) return cachedVector

        val token = env.read("OPENAI_KEY")
        val client = AiClient("https://api.openai.com/v1/embeddings", model, token)

        val request = EmbeddingRequest(
            model = model,
            input = content
        )

        for (i in 0..maxAttempts) {
            val result: EmbeddingResult? = client.request(request)
            if (result == null) {
                console.logWarning("No vector returned, resting for next attempt (attempts left: ${maxAttempts - i})")
                console.logWarning("content size: ${content.length}")
                delay(10.seconds)
                continue
            }
            val vector = result.data.firstOrNull()?.embedding ?: error("no embedding found")
            saveToCache(source.url.href, model, content.length, vector)
            return vector
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