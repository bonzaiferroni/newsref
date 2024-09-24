package streetlight.server.db.routes

import io.ktor.server.routing.*
import streetlight.model.Api
import streetlight.server.db.defaultRouting
import streetlight.server.db.services.*

fun Routing.dataRouting() {
//    defaultRouting(Api.area, AreaService())
//    defaultRouting(Api.event, EventService())
//    defaultRouting(Api.location, LocationService())
//    defaultRouting(Api.request, RequestService())

    userRouting(UserService())
}