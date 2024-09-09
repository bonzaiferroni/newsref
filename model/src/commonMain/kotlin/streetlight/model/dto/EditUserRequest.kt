package streetlight.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class EditUserRequest(
    val name: String? = null,
    val email: String? = null,
    val avatarUrl: String? = null,
    val venmo: String? = null,
    val deleteName: Boolean = false,
    val deleteEmail: Boolean = false,
    val deleteUser: Boolean = false,
) {
}