package newsref.server.db

import klutch.environment.Environment
import kotlinx.datetime.Clock
import klutch.db.model.User
import kabinet.model.UserRole
import klutch.server.base64ToByteArray
import klutch.server.hashPassword

fun createAdminUser(env: Environment) = User(
    name = env.read("ADMIN_NAME"),
    username = env.read("ADMIN_USERNAME"),
    hashedPassword = getHashedPassword(env),
    salt = env.read("ADMIN_SALT"),
    email = env.read("ADMIN_EMAIL"),
    roles = setOf(UserRole.ADMIN, UserRole.USER),
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
)

private fun getHashedPassword(env: Environment): String {
    val salt = env.read("ADMIN_SALT")
    val password = env.read("ADMIN_PASSWORD")
    return hashPassword(password, salt.base64ToByteArray())
}