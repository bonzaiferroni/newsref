package streetlight.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthInfo(
    val token: String,
)