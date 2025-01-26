package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import newsref.db.environment
import newsref.db.globalConsole
import newsref.db.services.ContentService
import newsref.db.services.VectorService
import newsref.db.services.NoteService
import newsref.db.services.VECTOR_MAX_CHARACTERS
import newsref.db.services.VECTOR_MIN_WORDS
import newsref.db.utils.profile
import newsref.krawly.clients.AiClient
import newsref.krawly.clients.EmbeddingRequest
import newsref.krawly.clients.EmbeddingResult
import newsref.model.data.Source
import java.io.File
import kotlin.time.Duration.Companion.minutes

private val console = globalConsole.getHandle("DistanceFinder")

class DistanceFinder(
	private val client: AiClient = AiClient(),
	private val vectorService: VectorService = VectorService(),
	private val contentService: ContentService = ContentService(),
) {

	fun start() {
		CoroutineScope(Dispatchers.IO).launch {
			console.logTrace("looking for sources")
			while (true) {
				findEmbeddings()
				delay(1.minutes)
			}
		}
	}

	private suspend fun findEmbeddings() {

		val source = vectorService.findNextJob() ?: return
		val content = contentService.getSourceContentText(source.id).take(VECTOR_MAX_CHARACTERS)
		if (content.length < VECTOR_MIN_WORDS) throw IllegalStateException("Content too small: ${content.length}/${source.contentCount}")
		val model = "text-embedding-3-small"
		val vector = fetchEmbeddings(source, model, content) ?: return
		vectorService.insertVector(source.id, model, vector)
		profile("distance_finder", true) {
			vectorService.generateDistances(source.id, model)
		}
	}

	private suspend fun fetchEmbeddings(source: Source, model: String, content: String): FloatArray? {
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