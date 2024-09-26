package newsref.model.data

import kotlinx.serialization.Serializable

@Serializable
data class Outlet(
    val id: Int = 0,
    val name: String? = null,
    val apex: String = "",
    val domains: Set<String> = emptySet(),
    val urlParams: Set<String> = emptySet(),
)