package streetlight.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String = "",
    val password: String? = null,
    val session: String? = null,
)