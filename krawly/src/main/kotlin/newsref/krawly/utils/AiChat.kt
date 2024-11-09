package newsref.krawly.utils

import newsref.krawly.models.AiConfig

class AiChat(
	private val config: AiConfig,
	private val script: String,
	private val client: AiClient,
) {
	val messages: MutableList<AiMessage> = mutableListOf(
		AiMessage(
			role = AiRole.SYSTEM,
			content = config.invocation
		),
		AiMessage(
			role = AiRole.SYSTEM,
			content = script
		)
	)

	suspend fun ask(question: String): String? {
		messages.add(AiMessage(AiRole.USER, question))
		val result = client.chat(messages, config.url, config.model, config.token)
		val message = result.choices.firstOrNull()?.message ?: return null
		messages.add(message)
		while(messages.size > 20) messages.removeFirst()
		return message.content
	}
}

