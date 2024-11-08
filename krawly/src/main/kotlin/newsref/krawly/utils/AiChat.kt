package newsref.krawly.utils

class AiChat(
	invocation: String,
	script: String,
	val client: AiClient,
) {
	val messages: MutableList<AiMessage> = mutableListOf(
		AiMessage(
			role = AiRole.SYSTEM,
			content = invocation
		),
		AiMessage(
			role = AiRole.SYSTEM,
			content = script
		)
	)

	suspend fun ask(question: String): String? {
		messages.add(AiMessage(AiRole.USER, question))
		val result = client.chat(messages)
		val message = result.choices.firstOrNull()?.message ?: return null
		messages.add(message)
		while(messages.size > 20) messages.removeFirst()
		return message.content
	}
}

