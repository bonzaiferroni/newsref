package newsref.model.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import newsref.model.core.RoleSet
import newsref.model.core.UserRole

@Serializable
data class UserDto(
    val username: String = "",
    val roles: RoleSet,
    val avatarUrl: String? = null,
    val createdAt: Instant = Instant.DISTANT_PAST,
    val updatedAt: Instant = Instant.DISTANT_PAST,
)

val UserDto.isAdmin: Boolean
    get() = UserRole.ADMIN in roles

val UserDto.isUser: Boolean
    get() = UserRole.USER in roles