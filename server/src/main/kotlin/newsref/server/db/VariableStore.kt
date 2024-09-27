package newsref.server.db

import kotlinx.datetime.Clock
import newsref.db.models.User

class VariableStore {

    val appSecret by lazy { getEnvVariable("NEWSREF_APP_SECRET") }
    val admin by lazy {
        User(
            name = getEnvVariable("NEWSREF_ADMIN_NAME"),
            username = getEnvVariable("NEWSREF_ADMIN_USERNAME"),
            hashedPassword = getHashedPassword(),
            salt = getEnvVariable("NEWSREF_ADMIN_SALT"),
            email = getEnvVariable("NEWSREF_ADMIN_EMAIL"),
            roles = "admin",
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
        )
    }

    private fun getEnvVariable(key: String): String {
        return System.getenv(key) ?: throw IllegalStateException("Missing environment variable: $key")
    }

    private fun getHashedPassword(): String {
        val salt = getEnvVariable("NEWSREF_ADMIN_SALT")
        val password = getEnvVariable("NEWSREF_ADMIN_PASSWORD")
        return hashPassword(password, salt.base64ToByteArray())
    }
}