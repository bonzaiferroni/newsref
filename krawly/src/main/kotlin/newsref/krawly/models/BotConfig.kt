package newsref.krawly.models

import kotlinx.serialization.Serializable

@Serializable
data class BotConfig(
	val name: String,
	val invocation: String,
	val noteScript: String? = null,
	val model: String,
	val isActive: Boolean = false,
)