package newsref.db.tables

import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import newsref.model.dto.UserDto
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
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}