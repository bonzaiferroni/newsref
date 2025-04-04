package newsref.server.db.services

import kotlinx.datetime.Clock
import newsref.db.DbService
import newsref.db.model.*
import newsref.db.tables.*
import newsref.db.utils.eqLowercase
import newsref.db.utils.toLocalDateTimeUtc
import newsref.model.core.UserRole
import newsref.model.data.EditUserRequest
import newsref.model.data.SignUpRequest
import newsref.model.dto.*
import newsref.model.utils.*
import newsref.server.db.*
import newsref.server.serverLog
import org.jetbrains.exposed.sql.*

class UserDtoService : DbService() {

    private fun readByUsername(username: String): User? =
        UserAspect.readFirst { UserTable.username.lowerCase() eq username.lowercase() }

    suspend fun readByUsernameOrEmail(usernameOrEmail: String): User? = dbQuery {
        UserAspect.readFirst {
            (UserTable.username.lowerCase() eq usernameOrEmail.lowercase()) or
                    (UserTable.email.lowerCase() eq usernameOrEmail.lowercase())
        }
    }

    suspend fun readIdByUsername(username: String) = dbQuery {
        UserTable.select(UserTable.id)
            .where { UserTable.username.eq(username) }
            .first()[UserTable.id].value
    }

    suspend fun readUserDto(username: String): UserDto {
        val user = readByUsernameOrEmail(username) ?: throw IllegalArgumentException("User not found")
        return UserDto(
            username = user.username,
            roles = user.roles,
            avatarUrl = user.avatarUrl,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
        )
    }

    suspend fun createUser(info: SignUpRequest) = dbQuery {
        validateUsername(info)
        validateEmail(info)
        validatePassword(info)

        val uniqueSalt = generateUniqueSalt()
        val passwordHashed = hashPassword(info.password, uniqueSalt)
        val now = Clock.System.now()

        UserTable.insert {
            it[name] = info.name
            it[username] = info.username
            it[hashedPassword] = passwordHashed
            it[salt] = uniqueSalt.toBase64()
            it[email] = info.email
            it[roles] = listOf(UserRole.USER.name)
            it[createdAt] = now.toLocalDateTimeUtc()
            it[updatedAt] = now.toLocalDateTimeUtc()
        }
    }

    private fun validateUsername(info: SignUpRequest) {
        if (!info.username.validUsernameLength) throw IllegalArgumentException("Username should be least 3 characters.")
        if (!info.username.validUsernameChars) throw IllegalArgumentException("Username has invalid characters.")
        val usernameTaken = UserAspect.any { UserTable.username.lowerCase() eq info.username.lowercase() }
        if (usernameTaken) throw IllegalArgumentException("Username already exists.")
    }

    private fun validateEmail(info: SignUpRequest) {
        val email = info.email ?: return // email is optional
        if (!info.email.validEmail) throw IllegalArgumentException("Invalid email.")
        val emailTaken = UserAspect.any { UserTable.email.lowerCase() eq email.lowercase() }
        if (emailTaken) throw IllegalArgumentException("Email already exists.")
    }

    private fun validatePassword(info: SignUpRequest) {
        if (!info.password.validPassword) throw IllegalArgumentException("Password is too weak.")
    }

    suspend fun getPrivateInfo(username: String) = dbQuery {
        val user = readByUsername(username) ?: throw IllegalArgumentException("User not found")
        user.toPrivateInfo()
    }

    suspend fun updateUser(username: String, info: EditUserRequest) = dbQuery {
        if (info.deleteUser) {
            serverLog.logInfo("UserService: Deleting user $username")
            UserTable.deleteWhere { UserTable.username.eqLowercase(username) }
        } else {
            serverLog.logInfo("UserService: Updating user $username")
            UserTable.update({UserTable.username.eqLowercase(username)}) {
                it[name] = info.name
                if (info.deleteName) it[name] = null
                it[email] = info.email
                if (info.deleteEmail) it[email] = null
                it[avatarUrl] = info.avatarUrl
                it[updatedAt] = Clock.System.now().toLocalDateTimeUtc()
            }
        }
    }
}