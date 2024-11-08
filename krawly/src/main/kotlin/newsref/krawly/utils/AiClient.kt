package newsref.krawly.utils

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class AiClient(
	private val model: String = "Qwen/Qwen2.5-1.5B-Instruct",
) {

	private val ktor = HttpClient(CIO) {
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
	}

	fun createChat(invocation: String, script: String) = AiChat(invocation, script, this)

	suspend fun chat(messages: List<AiMessage>) = ktor.post("http://localhost:8000/v1/chat/completions") {
		contentType(ContentType.Application.Json)
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
}

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
	val completionTokens: Int
)
