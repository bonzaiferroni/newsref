package streetlight.server.data

import io.ktor.server.application.ApplicationCall

fun ApplicationCall.getIdOrThrow(): Int {
    return this.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
}