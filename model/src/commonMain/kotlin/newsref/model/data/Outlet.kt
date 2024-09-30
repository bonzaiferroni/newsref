package newsref.model.data

import kotlinx.serialization.Serializable

@Serializable
data class Outlet(
    val id: Int = 0,
    val name: String? = null,
    val logo: String? = null,
    val robotsTxt: String? = null,                      // initialize
    val disallowed: Set<String>? = null,                // initialize
    val domains: Set<String> = emptySet(),              // initialize
    val urlParams: Set<String> = emptySet(),
)