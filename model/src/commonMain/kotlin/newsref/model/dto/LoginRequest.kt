package newsref.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val usernameOrEmail: String,
    val stayLoggedIn: Boolean,
    val password: String? = null,
    val refreshToken: String? = null,
)