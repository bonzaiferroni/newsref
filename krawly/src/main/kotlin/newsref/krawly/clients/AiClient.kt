package newsref.krawly.clients

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
import kotlinx.serialization.json.JsonObject
import newsref.db.globalConsole
import newsref.krawly.globalKtor
import newsref.krawly.models.AiConfig

private val console = globalConsole.getHandle("AiClient")

class AiClient(
    val url: String = "http://localhost:8000/v1/chat/completions",
    val model: String = "Qwen/Qwen2.5-3B-Instruct",
    val token: String? = null,
) {

    fun createChat(invocation: String, context: String? = null) = AiChat(invocation, context, this)

    suspend fun chat(messages: List<AiMessage>) = request<AiChatRequest, AiResponse>(
        AiChatRequest(
            model = model,
            messages = messages,
        )
    )

    suspend inline fun <reified Sent, reified Received> request(
        request: Sent,
    ): Received? {
        try {
            val response = globalKtor.post(url) {
                contentType(ContentType.Application.Json)
                token?.also { token ->
                    headers {
                        set(HttpHeaders.Authorization, "Bearer $token")
                    }
                }
                setBody(request)
            }

            if (response.status == HttpStatusCode.OK) {
                return response.body<Received>()
            } else {
                globalConsole.logError("AiClient", "Request failed:\n${response.body<JsonObject>()}")
                return null
            }
        } catch (e: HttpRequestTimeoutException) {
            globalConsole.logError("AiClient", "Request timed out")
            return null
        } catch (e: NoTransformationFoundException) {
            globalConsole.logError("AiClient", "no transformation? 😕")
            return null
        }
    }

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
    val messages: List<AiMessage>,
    val stream: Boolean = false,
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

@Suppress("ArrayInDataClass")
@Serializable
data class EmbeddingData(
    @SerialName("object")
    val type: String,
    val index: Int,
    val embedding: FloatArray
)