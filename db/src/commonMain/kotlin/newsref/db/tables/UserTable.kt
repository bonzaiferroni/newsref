package newsref.db.tables

import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import newsref.model.dto.UserInfo
import newsref.db.model.User
import newsref.db.utils.toLocalDateTimeUtc
import newsref.model.core.UserRole
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UserTable : LongIdTable("user") {
    val name = text("name").nullable()
    val username = text("username")
    val hashedPassword = text("hashed_password")
    val salt = text("salt")
    val email = text("email").nullable()
    val roles = array<String>("roles")
    val avatarUrl = text("avatar_url").nullable()
    val venmo = text("venmo").nullable()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}

class UserRow(id: EntityID<Long>) : LongEntity(id) {
    companion object : EntityClass<Long, UserRow>(UserTable)

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

fun UserRow.toData() = User(
    this.id.value,
    this.name,
    this.username,
    this.hashedPassword,
    this.salt,
    this.email,
    this.roles.map { UserRole.valueOf(it) }.toSet(),
    this.avatarUrl,
    this.venmo,
    this.createdAt.toInstant(UtcOffset.ZERO),
    this.updatedAt.toInstant(UtcOffset.ZERO),
)

fun UserRow.fromData(data: User) {
    name = data.name
    username = data.username
    hashedPassword = data.hashedPassword
    salt = data.salt
    email = data.email
    roles = data.roles.map { it.name }
    avatarUrl = data.avatarUrl
    venmo = data.venmo
    createdAt = data.createdAt.toLocalDateTime(TimeZone.UTC)
    updatedAt = Clock.System.now().toLocalDateTimeUtc()
}

fun UserRow.toInfo() = UserInfo(
    this.username,
    this.roles.map { UserRole.valueOf(it) }.toSet(),
    this.avatarUrl,
    this.venmo,
    this.createdAt.toInstant(UtcOffset.ZERO),
    this.updatedAt.toInstant(UtcOffset.ZERO),
)