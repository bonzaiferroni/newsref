package newsref.server.routes

import io.ktor.server.routing.Routing
import klutch.server.post
import newsref.db.services.LogDtoService
import newsref.model.Api

fun Routing.serveLogs(
    service: LogDtoService = LogDtoService()
) {
    post(Api.Logs) { sent, endpoint ->
        service.readLogs(sent)
    }
}