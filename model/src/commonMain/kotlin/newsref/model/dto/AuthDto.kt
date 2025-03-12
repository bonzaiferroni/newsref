package newsref.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthDto(
    val jwt: String,
    val refreshToken: String,
)