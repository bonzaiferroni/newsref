package streetlight.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginInfo(
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
    val token: String? = null,
)