package newsref.krawly.clients

import newsref.krawly.models.AiConfig

class AiChat(
    invocation: String,
    context: String? = null,
    private val client: AiClient,
) {
    private val messages: MutableList<AiMessage> = mutableListOf(
        AiMessage(
            role = AiRole.SYSTEM,
            content = invocation
        ),
    ).apply {
        if (context != null) {
            this.add(
                AiMessage(
                    role = AiRole.SYSTEM,
                    content = context
                )
            )
        }
    }

    suspend fun ask(question: String): String? {
        messages.add(AiMessage(AiRole.USER, question))
        val result = client.chat(messages) ?: return null
        val message = result.choices.firstOrNull()?.message ?: return null
        messages.add(message)
        while (messages.size > 20) messages.removeFirst()
        return message.content
    }
}

