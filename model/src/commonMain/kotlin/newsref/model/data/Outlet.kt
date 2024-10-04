package newsref.model.data

import kotlinx.serialization.Serializable

@Serializable
data class Outlet(
    val id: Int,
    val name: String?,
    val logo: String?,
    val robotsTxt: String?,
    val disallowed: Set<String>,
    val domains: Set<String>,
    val urlParams: Set<String>,
)