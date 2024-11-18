package newsref.krawly.utils

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import newsref.db.globalConsole
import newsref.krawly.models.AiConfig

private val console = globalConsole.getHandle("AiClient")

class AiClient(
	val url: String = "http://localhost:8000/v1/chat/completions",
	val model: String = "Qwen/Qwen2.5-3B-Instruct",
	val token: String? = null,
) {

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

	fun createChat(config: AiConfig, script: String) = AiChat(config, script, this)

	suspend fun chat(
		messages: List<AiMessage>,
		url: String = this.url,
		model: String = this.model,
		token: String? = this.token
	) = ktor.post(url) {
		contentType(ContentType.Application.Json)
		token?.also {
			headers {
				set(HttpHeaders.Authorization, "Bearer $it")
			}
		}
		setBody(AiChatRequest(
			model = model,
			messages = messages,
		))
	}.body<AiResponse>()

	suspend fun prompt(prompt: String) = ktor.post("http://localhost:8000/v1/completions") {
		contentType(ContentType.Application.Json)
		setBody(AiPromptRequest(
			model = model,
			prompt = prompt,
		))
	}.body<AiResponse>()

	suspend inline fun <reified Sent, reified Received> request(
		request: Sent,
		url: String = this.url,
		token: String? = this.token
	) = ktor.post(url) {
		contentType(ContentType.Application.Json)
		token?.also {
			headers {
				set(HttpHeaders.Authorization, "Bearer $it")
			}
		}
		setBody(request)
	}
		.also { if (it.status != HttpStatusCode.OK)
			globalConsole.logError("AiClient", "AiClient status: ${it.status}\n${it.bodyAsText()}")
		}
		.takeIf { it.status == HttpStatusCode.OK }?.body<Received>()
}

@Serializable
data class EmbeddingRequest(
	val input: String,
	val model: String,
)

@Serializable
data class AiChatRequest(
	val model: String,
	@SerialName("max_tokens")
	val maxTokens: Int? = null,
	val temperature: Double = .5,
	val messages: List<AiMessage>
)

@Serializable
data class AiPromptRequest(
	val model: String,
	@SerialName("max_tokens")
	val maxTokens: Int? = null,
	val temperature: Double = .5,
	val prompt: String
)

@Serializable
data class AiMessage(
	val role: AiRole,
	val content: String,
)

enum class AiRole {
	@SerialName("system")
	SYSTEM,

	@SerialName("user")
	USER,

	@SerialName("assistant")
	ASSISTANT,
}

@Serializable
data class AiResponse(
	val id: String,
	val created: Int,
	val model: String,
	val choices: List<Choice>,
	val usage: Usage
)

@Serializable
data class Choice(
	val index: Int,
	val text: String? = null,
	@SerialName("finish_reason")
	val finishReason: String,
	val message: AiMessage? = null,
)

@Serializable
data class Usage(
	@SerialName("prompt_tokens")
	val promptTokens: Int,
	@SerialName("total_tokens")
	val totalTokens: Int,
	@SerialName("completion_tokens")
	val completionTokens: Int = 0
)

@Serializable
data class EmbeddingResult(
	@SerialName("object")
	val type: String,
	val data: List<EmbeddingData>,
	val model: String,
	val usage: Usage
)

@Serializable
data class EmbeddingData(
	@SerialName("object")
	val type: String,
	val index: Int,
	val embedding: FloatArray
)