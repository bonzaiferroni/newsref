package newsref.krawly

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlin.time.Duration.Companion.seconds

private val key = System.getenv("OPENAI_KEY")
val bot = OpenAI(
    token = key,
//    timeout = Timeout(socket = 60.seconds),
    // additional configurations...
)

suspend fun OpenAI.ask(question: String): ChatCompletion {
    val chatCompletionRequest = ChatCompletionRequest(
        model = ModelId("gpt-3.5-turbo"),
        messages = listOf(
            ChatMessage(
                role = ChatRole.System,
                content = "You are a helpful assistant!"
            ),
            ChatMessage(
                role = ChatRole.User,
                content = question
            )
        )
    )
    return this.chatCompletion(chatCompletionRequest)
}


