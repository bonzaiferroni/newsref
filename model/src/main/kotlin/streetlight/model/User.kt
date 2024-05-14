package streetlight.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val password: String = "",
)