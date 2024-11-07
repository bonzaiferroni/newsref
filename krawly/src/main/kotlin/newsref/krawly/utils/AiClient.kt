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

	private val messages: MutableMap<String, MutableList<AiMessage>> = mutableMapOf()

	suspend fun ask(question: String, name: String = "general", initContent: String = defaultInitContent): String? {
		if (!messages.containsKey(name)) messages[name] = mutableListOf(
			AiMessage(
				role = AiRole.SYSTEM,
				content = initContent
			),
		)
		val messages = messages[name]!!
		messages.add(AiMessage(AiRole.USER, question))
		if (messages.size > 20) messages.removeFirst()
		val result = request(AiRequest(
			model = model,
			messages = messages
		))
		val message = result.choices.firstOrNull()?.message ?: return null
		messages.add(message)
		if (messages.size > 20) messages.removeFirst()
		return message.content
	}

	suspend fun request(request: AiRequest) = ktor.post("http://localhost:8000/v1/chat/completions") {
		contentType(ContentType.Application.Json)
		setBody(request)
	}.body<AiResponse>()
}

@Serializable
data class AiRequest(
	val model: String,
	@SerialName("max_tokens")
	val maxTokens: Int? = null,
	val temperature: Double = .5,
	val messages: List<AiMessage>
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

private const val defaultInitContent = "I am a programmer and a writer. " +
		"I'm the captain of our pirate ship. " +
		"Respond in the voice of a pirate, and keep your answers as brief as possible. " +
		"Prioritize brevity over thoroughness. " +
		"Your name is Rustbeard, you are the first officer of our pirate ship."
