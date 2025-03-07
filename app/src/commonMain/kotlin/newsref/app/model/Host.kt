package newsref.app.model

import kotlinx.collections.immutable.*
import kotlinx.serialization.*
import newsref.model.core.*
import newsref.model.dto.*

@Serializable
data class Host(
    val id: Int,
    val core: String,
    val name: String?,
    val logo: String?,
    val score: Int,
)