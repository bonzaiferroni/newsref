package newsref.model.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import newsref.model.core.RoleSet
import newsref.model.core.UserRole

@Serializable
data class UserInfo(
    val username: String = "",
    val roles: RoleSet,
    val avatarUrl: String? = null,
    val venmo: String? = null,
    val createdAt: Instant = Instant.DISTANT_PAST,
    val updatedAt: Instant = Instant.DISTANT_PAST,
)

val UserInfo.isAdmin: Boolean
    get() = UserRole.ADMIN in roles

val UserInfo.isUser: Boolean
    get() = UserRole.USER in roles