package newsref.server.db

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import newsref.db.models.User
import newsref.model.dto.AuthInfo
import newsref.model.dto.LoginRequest
import newsref.db.Log
import newsref.db.models.SessionToken
import newsref.model.core.RoleSet
import newsref.model.utils.deobfuscate
import newsref.server.db.services.SessionTokenService
import newsref.server.db.services.UserService
import newsref.server.plugins.createJWT
import newsref.server.serverLog
import java.security.SecureRandom
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

suspend fun ApplicationCall.authorize() {
    val loginRequest = this.receiveNullable<LoginRequest>() ?: return
    val userService = UserService()
    val user = userService.findByUsernameOrEmail(loginRequest.username)
    if (user == null) {
        serverLog.logInfo("authorize: Invalid username from ${loginRequest.username}")
        this.respond(HttpStatusCode.Unauthorized, "Invalid username")
        return
    }
    loginRequest.password?.let {
        val password = it.deobfuscate()
        val authInfo = user.testPassword(loginRequest.username, password, user.roles)
        if (authInfo == null) {
            serverLog.logInfo("authorize: Invalid password attempt from ${loginRequest.username}")
            return
        }
        serverLog.logInfo("authorize: password login by ${loginRequest.username}")
        this.respond(HttpStatusCode.OK, authInfo)
        return
    }
    loginRequest.session?.let {
        val authInfo = user.testToken(loginRequest.username, it, user.roles)
        if (authInfo == null) {
            serverLog.logInfo("authorize: Invalid password attempt from ${loginRequest.username}")
            this.respond(HttpStatusCode.Unauthorized, "Invalid token")
            return
        }
        serverLog.logInfo("authorize: session login by ${loginRequest.username}")
        this.respond(HttpStatusCode.OK, authInfo)
        return
    }
    this.respond(HttpStatusCode.Unauthorized, "Missing password or token")
}

suspend fun User.testPassword(username: String, password: String, roles: RoleSet): AuthInfo? {
    val byteArray = this.salt.base64ToByteArray()
    val hashedPassword = hashPassword(password, byteArray)
    if (hashedPassword != this.hashedPassword) {
        return null
    }

    val sessionToken = this.createSessionToken()
    val jwt = createJWT(username, roles)
    return AuthInfo(jwt, sessionToken)
}

suspend fun User.testToken(username: String, sessionToken: String, roles: RoleSet): AuthInfo? {
    val service = SessionTokenService()
    val sessionTokenEntity = service.findByToken(sessionToken)
        ?: return null
    if (sessionTokenEntity.userId != this.id) {
        return null
    }
    val jwt = createJWT(username, roles)
    return AuthInfo(jwt)
}

suspend fun User.createSessionToken(): String {
    val token = UUID.randomUUID().toString()
    val service = SessionTokenService()
    service.create(
        SessionToken(
        userId = this.id,
        token = token,
        createdAt = System.currentTimeMillis(),
        expiresAt = System.currentTimeMillis() + 60000,
        issuer = "http://localhost:8080/"
    )
    )
    return token
}

fun hashPassword(password: String, salt: ByteArray): String {
    val iterations = 65536
    val keyLength = 256
    val spec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLength)
    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    val hash = factory.generateSecret(spec).encoded
    return Base64.getEncoder().encodeToString(hash)
}

fun generateSalt(): ByteArray {
    val random = SecureRandom()
    val salt = ByteArray(16)
    random.nextBytes(salt)
    return salt
}

fun ByteArray.toBase64(): String {
    return Base64.getEncoder().encodeToString(this)
}

fun String.base64ToByteArray(): ByteArray {
    return Base64.getDecoder().decode(this)
}
