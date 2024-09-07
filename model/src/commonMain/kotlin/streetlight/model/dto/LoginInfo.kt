package streetlight.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginInfo(
    val username: String = "",
    val password: String? = null,
    val session: String? = null,
)