package newsref.server.utilities

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import newsref.model.Endpoint
import newsref.model.EndpointParam
import newsref.model.GetByIdEndpoint
import newsref.model.PostEndpoint
import newsref.server.extensions.getIdOrThrow

fun <Returned, E : Endpoint<Returned>> Route.get(
    endpoint: E,
    block: suspend RoutingContext.(E) -> Returned?
) = get(endpoint.path) {
    standardResponse { block(endpoint) }
}

fun <Returned, E : GetByIdEndpoint<Returned>> Route.getById(
    endpoint: E,
    block: suspend RoutingContext.(Long, E) -> Returned?
) = get(endpoint.serverIdTemplate) {
    val id = call.getIdOrThrow { it.toLongOrNull() }
    standardResponse { block(id, endpoint) }
}

inline fun <Returned, reified Sent : Any, E : PostEndpoint<Sent, Returned>> Route.post(
    endpoint: E,
    noinline block: suspend RoutingContext.(Sent, E) -> Returned?
) = post(endpoint.path) {
    val sentValue = call.receive<Sent>()
    standardResponse { block(sentValue, endpoint) }
}

suspend fun <T> RoutingContext.standardResponse(block: suspend () -> T?): Unit =
    try {
        val value = block()
        if (value != null) {
            call.respond(HttpStatusCode.OK, value)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    } catch (e: MissingParameterException) {
        call.respond(HttpStatusCode.BadRequest, "Missing required parameter: ${e.param}")
    }

fun <T> EndpointParam<T>.readFromCallOrNull(call: RoutingCall): T? {
    val str = call.request.queryParameters[this.key] ?: return null
    return this.read(str)
}

fun <T> EndpointParam<T>.readFromCall(call: RoutingCall): T =
    this.readFromCallOrNull(call) ?: throw MissingParameterException(this.key)

class MissingParameterException(val param: String) : Exception("Missing required parameter: $param")
