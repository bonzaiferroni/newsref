package newsref.server.extensions

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import klutch.server.CLAIM_ROLES
import klutch.server.CLAIM_USERNAME

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