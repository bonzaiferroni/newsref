package newsref.server.db

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import newsref.db.model.User
import newsref.model.dto.AuthDto
import newsref.model.dto.LoginRequest
import newsref.db.tables.UserTable
import newsref.model.utils.deobfuscate
import newsref.server.db.services.RefreshTokenService
import newsref.server.db.services.UserDtoService
import newsref.server.plugins.createJWT
import newsref.server.serverLog
import java.security.SecureRandom
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

suspend fun ApplicationCall.authorize(service: UserDtoService = UserDtoService()) {
    val loginRequest = this.receiveNullable<LoginRequest>() ?: return
    val claimedUser = service.findByUsernameOrEmail(loginRequest.usernameOrEmail)
    if (claimedUser == null) {
        serverLog.logInfo("authorize: Invalid username from ${loginRequest.usernameOrEmail}")
        this.respond(HttpStatusCode.Unauthorized, "Invalid username")
        return
    }
    loginRequest.password?.let {
        val givenPassword = it.deobfuscate()
        val authInfo = testPassword(claimedUser, givenPassword, loginRequest.stayLoggedIn)
        if (authInfo == null) {
            serverLog.logInfo("authorize: Invalid password attempt from ${loginRequest.usernameOrEmail}")
            return
        }
        serverLog.logInfo("authorize: password login by ${loginRequest.usernameOrEmail}")
        this.respond(HttpStatusCode.OK, authInfo)
        return
    }
    loginRequest.refreshToken?.let {
        val authInfo = testToken(claimedUser, it, loginRequest.stayLoggedIn)
        if (authInfo == null) {
            serverLog.logInfo("authorize: Invalid password attempt from ${loginRequest.usernameOrEmail}")
            this.respond(HttpStatusCode.Unauthorized, "Invalid token")
            return
        }
        serverLog.logInfo("authorize: session login by ${loginRequest.usernameOrEmail}")
        this.respond(HttpStatusCode.OK, authInfo)
        return
    }
    this.respond(HttpStatusCode.Unauthorized, "Missing password or token")
}

suspend fun testPassword(claimedUser: User, givenPassword: String, stayLoggedIn: Boolean): AuthDto? {
    val byteArray = claimedUser.salt.base64ToByteArray()
    val hashedPassword = hashPassword(givenPassword, byteArray)
    if (hashedPassword != claimedUser.hashedPassword) {
        return null
    }

    val sessionToken = createRefreshToken(claimedUser, stayLoggedIn)
    val jwt = createJWT(claimedUser.username, claimedUser.roles)
    return AuthDto(jwt, sessionToken)
}

suspend fun testToken(claimedUser: User, refreshToken: String, stayLoggedIn: Boolean): AuthDto? {
    val service = RefreshTokenService()
    val cachedToken = service.readToken(refreshToken)
        ?: return null
    if (cachedToken.userId != claimedUser.id) {
        return null
    }
    if (cachedToken.isExpired) {
        service.deleteToken(refreshToken)
        return null
    }
    val returnedToken = if (cachedToken.needsRotating) {
        service.deleteToken(refreshToken)
        createRefreshToken(claimedUser, stayLoggedIn)
    } else {
        refreshToken
    }
    val jwt = createJWT(claimedUser.username, claimedUser.roles)
    return AuthDto(jwt, returnedToken)
}

fun generateToken() = UUID.randomUUID().toString()

suspend fun createRefreshToken(user: User, stayLoggedIn: Boolean): String {
    val service = RefreshTokenService()
    val generatedToken = generateToken()
    service.createToken(user.id, generatedToken, stayLoggedIn)
    return generatedToken
}

fun hashPassword(password: String, salt: ByteArray): String {
    val iterations = 65536
    val keyLength = 256
    val spec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLength)
    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    val hash = factory.generateSecret(spec).encoded
    return Base64.getEncoder().encodeToString(hash)
}

fun generateUniqueSalt(): ByteArray {
    while (true) {
        val salt = generateSalt()
        val isUnique = UserTable
            .select(UserTable.salt)
            .where { UserTable.salt.eq(salt.toBase64()) }
            .toList().isEmpty()
        if (isUnique) return salt
    }
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
