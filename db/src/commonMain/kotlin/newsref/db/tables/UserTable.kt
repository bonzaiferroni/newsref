package newsref.db.tables

import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import newsref.model.dto.UserInfo
import newsref.db.models.User
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UserTable : IntIdTable("user") {
    val name = text("name").nullable()
    val username = text("username")
    val hashedPassword = text("hashed_password")
    val salt = text("salt")
    val email = text("email").nullable()
    val roles = text("roles").default("user")
    val avatarUrl = text("avatar_url").nullable()
    val venmo = text("venmo").nullable()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}

class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : EntityClass<Int, UserEntity>(UserTable)

    var name by UserTable.name
    var username by UserTable.username
    var hashedPassword by UserTable.hashedPassword
    var salt by UserTable.salt
    var email by UserTable.email
    var roles by UserTable.roles
    var avatarUrl by UserTable.avatarUrl
    var venmo by UserTable.venmo
    var createdAt by UserTable.createdAt
    var updatedAt by UserTable.updatedAt
}

fun UserEntity.toData() = User(
    this.id.value,
    this.name,
    this.username,
    this.hashedPassword,
    this.salt,
    this.email,
    this.roles,
    this.avatarUrl,
    this.venmo,
    this.createdAt.toInstant(UtcOffset.ZERO),
    this.updatedAt.toInstant(UtcOffset.ZERO),
)

fun UserEntity.fromData(data: User) {
    name = data.name
    username = data.username
    hashedPassword = data.hashedPassword
    salt = data.salt
    email = data.email
    roles = data.roles
    avatarUrl = data.avatarUrl
    venmo = data.venmo
    createdAt = data.createdAt.toLocalDateTime(TimeZone.UTC)
    updatedAt = data.updatedAt.toLocalDateTime(TimeZone.UTC)
}

fun UserEntity.toInfo() = UserInfo(
    this.username,
    this.roles,
    this.avatarUrl,
    this.venmo,
    this.createdAt.toInstant(UtcOffset.ZERO),
    this.updatedAt.toInstant(UtcOffset.ZERO),
)