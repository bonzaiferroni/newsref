package newsref.db.tables

import newsref.db.core.Aspect
import newsref.db.utils.toInstantUtc
import newsref.model.data.UserRole
import newsref.model.data.User
import org.jetbrains.exposed.sql.ResultRow

object UserDtoAspect : Aspect<UserDtoAspect, User>(
    UserTable,
    ResultRow::toUserDto
) {
    val id = add(UserTable.id)
    val username = add(UserTable.username)
    val roles = add(UserTable.roles)
    val avatarUrl = add(UserTable.avatarUrl)
    val createdAt = add(UserTable.createdAt)
    val updatedAt = add(UserTable.updatedAt)
}

fun ResultRow.toUserDto() = User(
    username = this[UserDtoAspect.username],
    roles = this[UserDtoAspect.roles].map { UserRole.valueOf(it) }.toSet(),
    avatarUrl = this[UserDtoAspect.avatarUrl],
    createdAt = this[UserDtoAspect.createdAt].toInstantUtc(),
    updatedAt = this[UserDtoAspect.updatedAt].toInstantUtc(),
)

