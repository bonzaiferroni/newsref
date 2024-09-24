package newsref.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val username: String = "",
    val password: String = "",
    val email: String? = null,
    val name: String? = null
) {

}