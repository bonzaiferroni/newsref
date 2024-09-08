package streetlight.model.core

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int = 0,
    val name: String? = "",
    val username: String = "",
    val hashedPassword: String = "",
    val salt: String = "",
    val email: String? = null,
    val roles: String = "",
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val avatarUrl: String? = null,
    val venmo: String? = null,
)

// TODO: Move to server module