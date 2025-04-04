package newsref.model.data

import kotlinx.serialization.Serializable

@Serializable
data class Host(
    val id: Int,
    val core: String,
    val name: String?,
    val logo: String?,
    val score: Int,
)