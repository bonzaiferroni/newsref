package newsref.server

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.dao.IntEntity
import newsref.model.Endpoint
import newsref.model.core.IdModel
import newsref.db.DataService
import newsref.server.extensions.getIdOrThrow
import newsref.server.extensions.testRole
import newsref.server.plugins.ROLE_ADMIN
import newsref.server.plugins.authenticateJwt

inline fun <reified Data : IdModel, DataEntity : IntEntity> Routing.defaultRouting(
    endpoint: Endpoint, service: DataService<Data, DataEntity>
) {
    applyGet(endpoint, service)
    applyGetAll(endpoint, service)
    authenticateJwt {
        applyPost(endpoint, service)
        applyPut(endpoint, service)
        applyDelete(endpoint, service)
    }
}

inline fun <reified Data : IdModel, DataEntity : IntEntity> Routing.applyGet(
    endpoint: Endpoint,
    service: DataService<Data, DataEntity>
) {
    get(endpoint.serverIdTemplate) {
        serverLog.logDebug("Routing: GET ${endpoint.serverIdTemplate}")
        val id = call.getIdOrThrow()
        val data = service.read(id)
        if (data != null) {
            call.respond(HttpStatusCode.OK, data)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}

inline fun <reified Data : IdModel, DataEntity : IntEntity> Routing.applyGetAll(
    endpoint: Endpoint,
    service: DataService<Data, DataEntity>
) {
    get(endpoint.path) {
        serverLog.logDebug("Routing: GET ${endpoint.path}")
        val search = call.parameters["search"] ?: ""
        val count = call.parameters["limit"]?.toIntOrNull() ?: 10
        val data = if (search.isBlank()) {
            service.readAll()
        } else {
            service.search(service.getSearchOp(search), count)
        }
        call.respond(HttpStatusCode.OK, data)
    }
}

inline fun <reified Data : IdModel, DataEntity : IntEntity> Route.applyPost(
    endpoint: Endpoint,
    service: DataService<Data, DataEntity>
) {
    post(endpoint.path) {
        serverLog.logDebug("Routing: POST ${endpoint.path}")
        if (!call.testRole(ROLE_ADMIN)) {
            call.respond(HttpStatusCode.Forbidden)
            return@post
        }
        val data = call.receive<Data>()
        val id = service.create(data)
        if (id == -1) {
            serverLog.logInfo("Routing: POST unable to create data: ${endpoint.path}")
        }
        call.respond(HttpStatusCode.Created, id)
    }
}

inline fun <reified Data : IdModel, DataEntity : IntEntity> Route.applyPut(
    endpoint: Endpoint,
    service: DataService<Data, DataEntity>
) {
    put(endpoint.path) {
        serverLog.logDebug("Routing: PUT ${endpoint.path}")
        if (!call.testRole(ROLE_ADMIN)) {
            call.respond(HttpStatusCode.Forbidden)
            return@put
        }
        var data = call.receive<Data>()
        data = service.update(data)
        call.respond(HttpStatusCode.OK, data)
    }
}

inline fun <reified Data : IdModel, DataEntity : IntEntity> Route.applyDelete(
    endpoint: Endpoint,
    service: DataService<Data, DataEntity>
) {
    delete(endpoint.serverIdTemplate) {
        serverLog.logDebug("Routing: DELETE ${endpoint.serverIdTemplate}")
        if (!call.testRole(ROLE_ADMIN)) {
            call.respond(HttpStatusCode.Forbidden)
            return@delete
        }
        val id = call.getIdOrThrow()
        service.delete(id)
        call.respond(HttpStatusCode.OK, true)
    }
}