package newsref.model.data

import kotlinx.serialization.Serializable

@Serializable
data class Host(
	val id: Int = 0,
	val nexusId: Int? = null,
	val core: String,
	val name: String? = null,
	val logo: String? = null,
	val robotsTxt: String? = null,
	val isRedirect: Boolean? = null,
	val score: Int,
	val domains: Set<String>,
	val bannedPaths: Set<String> = emptySet(),
	val junkParams: Set<String> = emptySet(),
	val navParams: Set<String> = emptySet(),
) {
	fun hasNexus(other: Host) = nexusId != null && nexusId == other.nexusId
	fun isExternalTo(other: Host) = !hasNexus(other) && core != other.core
}