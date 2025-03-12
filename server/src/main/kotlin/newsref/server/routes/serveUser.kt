package newsref.server.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import newsref.db.services.*
import newsref.model.Api
import newsref.server.db.services.*
import newsref.server.utilities.*
import kotlin.time.Duration.Companion.days

fun Routing.serveUsers(service: UserDtoService = UserDtoService()) {

}