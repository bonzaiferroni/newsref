package newsref.model.data

import kotlinx.serialization.Serializable

@Serializable
data class Outlet(
    val id: Int = 0,
    val apex: String = "",
    val name: String? = null,
    val robotsTxt: String? = null,
    val sitemapXml: String? = null,
    val disallowed: Set<String> = emptySet(),
    val domains: Set<String> = emptySet(),
    val urlParams: Set<String> = emptySet(),
)