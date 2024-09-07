package streetlight.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthInfo(
    val jwt: String? = null,
    val session: String? = null,
)