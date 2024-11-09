package newsref.krawly.models

import kotlinx.serialization.Serializable

@Serializable
data class AiConfig(
	val name: String,
	val invocation: String,
	val script: String? = null,
	val model: String,
	val url: String,
	val token: String? = null,
	val voice: String? = null,
	val status: BotStatus = BotStatus.ASLEEP,
)

enum class BotStatus {
	ASLEEP,
	ACTIVE,
}

@Serializable
data class AiSpeech(
	val voice: String,
	val content: String,
)