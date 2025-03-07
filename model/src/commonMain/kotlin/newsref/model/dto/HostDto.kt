package newsref.model.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.*
import newsref.model.core.*

@Serializable
data class HostDto(
    val id: Int,
    val core: String,
    val name: String?,
    val logo: String?,
    val score: Int,
)