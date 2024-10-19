package newsref.model.data

import kotlinx.serialization.Serializable

@Serializable
data class Host(
	val id: Int = 0,
	val core: String,
	val name: String? = null,
	val logo: String? = null,
	val robotsTxt: String? = null,
	val isRedirect: Boolean? = null,
	val domains: Set<String>,
	val bannedPaths: Set<String> = emptySet(),
	val junkParams: Set<String> = emptySet(),
)