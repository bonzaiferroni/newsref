package newsref.app.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import newsref.model.core.RoleSet

@Serializable
data class User(
    val username: String,
    val roles: RoleSet,
    val avatarUrl: String? = null,
    val createdAt: Instant = Instant.DISTANT_PAST,
    val updatedAt: Instant = Instant.DISTANT_PAST,
)