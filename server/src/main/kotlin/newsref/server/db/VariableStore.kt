package newsref.server.db

import kotlinx.datetime.Clock
import newsref.db.Environment
import newsref.db.models.User
import newsref.model.core.UserRole

class VariableStore(
    private val env: Environment
) {

    val appSecret by lazy { env.read("NEWSREF_APP_SECRET") }
    val admin by lazy {
        User(
            name = env.read("NEWSREF_ADMIN_NAME"),
            username = env.read("NEWSREF_ADMIN_USERNAME"),
            hashedPassword = getHashedPassword(),
            salt = env.read("NEWSREF_ADMIN_SALT"),
            email = env.read("NEWSREF_ADMIN_EMAIL"),
            roles = setOf(UserRole.ADMIN, UserRole.USER),
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
        )
    }

    private fun getHashedPassword(): String {
        val salt = env.read("NEWSREF_ADMIN_SALT")
        val password = env.read("NEWSREF_ADMIN_PASSWORD")
        return hashPassword(password, salt.base64ToByteArray())
    }
}