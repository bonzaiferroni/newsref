package newsref.krawly.agents

import newsref.db.core.*
import newsref.db.*
import newsref.krawly.clients.*
import newsref.model.data.*
import java.io.File

private val console = globalConsole.getHandle("VectorClient")

class VectorClient(
    private val client: AiClient = AiClient(),
) {

    suspend fun fetchVector(source: Source, model: String, content: String): FloatArray? {
        val cachedVector = readFromCache(source.url.href, model, content.length)
        if (cachedVector != null) return cachedVector

        val token = environment["OPENAI_KEY"]
            ?: throw IllegalStateException("Token missing from environment: OPENAI_KEY")

        val request = EmbeddingRequest(
            model = model,
            input = content
        )
        val result: EmbeddingResult? = client.request(request, "https://api.openai.com/v1/embeddings", token)
            ?: return null
        val vector = result?.data?.firstOrNull()?.embedding ?: throw IllegalStateException("no embedding found")
        saveToCache(source.url.href, model, content.length, vector)
        return vector
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