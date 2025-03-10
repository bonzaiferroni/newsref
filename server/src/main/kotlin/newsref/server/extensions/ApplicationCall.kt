package newsref.server.extensions

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import newsref.server.plugins.CLAIM_ROLES
import newsref.server.plugins.CLAIM_USERNAME

fun ApplicationCall.getIdOrThrow() = getIdOrThrow { it.toIntOrNull() }

fun <T: Any> ApplicationCall.getIdOrThrow(convertId: (String) -> T?): T {
    return this.parameters["id"]?.let { convertId(it) } ?: throw IllegalArgumentException("Id not found")
}

fun ApplicationCall.getClaim(name: String): String {
    return this.principal<JWTPrincipal>()?.payload?.getClaim(name)?.asString() ?: ""
}

fun ApplicationCall.testRole(role: String): Boolean {
    return this.getClaim(CLAIM_ROLES).contains(role)
}

fun ApplicationCall.getUsername(): String {
    return this.getClaim(CLAIM_USERNAME)
}

suspend inline fun <reified T: Any> ApplicationCall.okData(data: T) {
    this.respond(HttpStatusCode.OK, data)
}