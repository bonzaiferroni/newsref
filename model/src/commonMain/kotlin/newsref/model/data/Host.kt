package newsref.model.data

import kotlinx.serialization.Serializable

@Serializable
data class Host(
	val id: Int,
	val apex: String,
	val name: String?,
	val logo: String?,
	val robotsTxt: String?,
	val isRedirect: Boolean?,
	val domains: Set<String>,
	val disallowed: Set<String>,
	val junkParams: Set<String>,
)