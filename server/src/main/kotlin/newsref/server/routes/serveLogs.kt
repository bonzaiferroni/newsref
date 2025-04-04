package newsref.server.routes

import io.ktor.server.routing.Routing
import newsref.db.services.LogDtoService
import newsref.model.Api
import newsref.server.utilities.*

fun Routing.serveLogs(
    service: LogDtoService = LogDtoService()
) {
    post(Api.Logs) { sent, endpoint ->
        service.readLogs(sent)
    }
}