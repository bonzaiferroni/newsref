package newsref.server.routes

import io.ktor.server.routing.*
import newsref.server.db.services.UserService

fun Routing.dataRouting() {
//    defaultRouting(Api.area, AreaService())
//    defaultRouting(Api.event, EventService())
//    defaultRouting(Api.location, LocationService())
//    defaultRouting(Api.request, RequestService())

    userRouting(UserService())
    sourceRouting()
    serveChapters()
    serveHosts()
}