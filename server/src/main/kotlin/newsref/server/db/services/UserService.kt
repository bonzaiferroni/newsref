package newsref.server.db.services

import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.or
import newsref.model.dto.EditUserRequest
import newsref.model.dto.SignUpRequest
import newsref.model.dto.UserInfo
import newsref.model.utils.validEmail
import newsref.model.utils.validPassword
import newsref.model.utils.validUsernameChars
import newsref.model.utils.validUsernameLength
import newsref.db.DataService
import newsref.server.db.generateSalt
import newsref.server.db.hashPassword
import newsref.server.db.toBase64
import newsref.db.tables.*
import newsref.db.model.User
import newsref.db.model.toPrivateInfo
import newsref.db.utils.nowToLocalDateTimeUtc
import newsref.model.core.UserRole
import newsref.server.serverLog

class UserService : DataService<User, Long, UserRow>(
    UserRow,
    {user -> user.id},
    UserRow::fromData,
    UserRow::toData
) {

    fun findByUsername(username: String): User? =
        UserRow.find { UserTable.username.lowerCase() eq username.lowercase() }.firstOrNull()?.toData()

    suspend fun findByUsernameOrEmail(usernameOrEmail: String): User? = dbQuery {
        UserRow.find {
            (UserTable.username.lowerCase() eq usernameOrEmail.lowercase()) or
            (UserTable.email.lowerCase() eq usernameOrEmail.lowercase())
        }.firstOrNull()?.toData()
    }

    suspend fun getUserInfo(username: String): UserInfo {
        val user = findByUsernameOrEmail(username) ?: throw IllegalArgumentException("User not found")
        return UserInfo(
            username = user.username,
            roles = user.roles,
            avatarUrl = user.avatarUrl,
            venmo = user.venmo,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
        )
    }

    suspend fun createUser(info: SignUpRequest) = dbQuery {
        validateUsername(info)
        validateEmail(info)
        validatePassword(info)
        val salt = generateSalt()

        val user = User(
            name = info.name,
            username = info.username,
            hashedPassword = hashPassword(info.password, salt),
            salt = salt.toBase64(),
            email = info.email,
            roles = setOf(UserRole.USER),
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
            avatarUrl = null,
            venmo = null
        )
        create(user)
    }

    private fun validateUsername(info: SignUpRequest) {
        if (!info.username.validUsernameLength) throw IllegalArgumentException("Username should be least 3 characters.")
        if (!info.username.validUsernameChars) throw IllegalArgumentException("Username has invalid characters.")
        val existingUsername = UserRow.find { UserTable.username.lowerCase() eq info.username.lowercase() }.any()
        if (existingUsername) throw IllegalArgumentException("Username already exists.")
    }

    private fun validateEmail(info: SignUpRequest) {
        val email = info.email ?: return // email is optional
        if (!info.email.validEmail) throw IllegalArgumentException("Invalid email.")
        val existingEmail = UserRow.find { UserTable.email.lowerCase() eq email.lowercase() }.any()
        if (existingEmail) throw IllegalArgumentException("Email already exists.")
    }

    private fun validatePassword(info: SignUpRequest) {
        if (!info.password.validPassword) throw IllegalArgumentException("Password is too weak.")
    }

    suspend fun getPrivateInfo(username: String) = dbQuery {
        val user = findByUsername(username) ?: throw IllegalArgumentException("User not found")
        user.toPrivateInfo()
    }

    suspend fun updateUser(username: String, info: EditUserRequest) = dbQuery {
        if (info.deleteUser) {
            serverLog.logInfo("UserService: Deleting user $username")
            UserRow.findSingleByAndUpdate(UserTable.username.lowerCase() eq username.lowercase()) { entity ->
                SessionTokenEntity.find { SessionTokenTable.user eq entity.id }.forEach { it.delete() }
            }
        } else {
            serverLog.logInfo("UserService: Updating user $username")
            UserRow.findSingleByAndUpdate(UserTable.username.lowerCase() eq username.lowercase()) { entity ->
                entity.name = info.name
                if (info.deleteName) entity.name = null
                entity.email = info.email
                if (info.deleteEmail) entity.email = null
                entity.venmo = info.venmo
                entity.avatarUrl = info.avatarUrl
                entity.updatedAt = Clock.nowToLocalDateTimeUtc()
                // TODO validate email
            }
        }
    }

    // suspend fun resetPassword
}

