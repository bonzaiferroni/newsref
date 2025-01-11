package newsref.krawly.clients

import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import newsref.db.environment

class HfClient(
	model: String
) {
	private val client = AiClient(
		// url = "https://api-inference.huggingface.co/models/$model"
		url = "http://localhost:8000/"
	)

	val ktor = HttpClient(CIO) {
		install(ContentNegotiation) {
			json(Json {
				prettyPrint = true
				isLenient = true
				ignoreUnknownKeys = true
			})
		}
		defaultRequest {
			headers {
				set(HttpHeaders.ContentType, "application/json")
			}
		}
		engine {
			requestTimeout = 120_000 // Timeout in milliseconds (30 seconds here)
		}
		install(HttpTimeout) {
			requestTimeoutMillis = 120_000 // Set request timeout
			connectTimeoutMillis = 120_000 // Set connection timeout
			socketTimeoutMillis = 120_000  // Set socket timeout
		}
	}

	private val token = environment["HF_KEY"]

	fun request(text: String) {
		
	}
}

data class HfEmbeddingsResponse(
	val status: Int,
	val error: String,
	val vector: FloatArray
)

@Serializable
data class HfEmbeddingsRequest(
	val inputs: String,
	val normalize: Boolean? = null,
	val promptName: String? = null,
	val truncate: Boolean? = null,
	val truncationDirection: TruncationDirection? = null,
)

enum class TruncationDirection {
	Left,
	Right
}