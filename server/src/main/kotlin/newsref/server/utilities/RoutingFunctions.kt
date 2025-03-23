package newsref.server.utilities

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import newsref.model.Endpoint
import newsref.model.EndpointParam
import newsref.model.PostEndpoint
import newsref.server.extensions.getIdOrThrow

fun <T: Endpoint> Routing.getByPath(endpoint: T, block: suspend RoutingContext.(T) -> Unit) =
    get(endpoint.path) {
        try {
            block(endpoint)
        } catch (e: MissingParameterException) {
            call.respond(HttpStatusCode.BadRequest, "Missing required parameter: ${e.param}")
        }
    }

fun <T: Endpoint> Routing.getById(endpoint: T, block: suspend RoutingContext.(Long, T) -> Unit) =
    get(endpoint.serverIdTemplate) {
        val id = call.getIdOrThrow { it.toLongOrNull() }
        try {
            block(id, endpoint)
        } catch (e: MissingParameterException) {
            call.respond(HttpStatusCode.BadRequest, "Missing required parameter: ${e.param}")
        }
    }

inline fun <reified Sent: Any, E: PostEndpoint<Sent>> Routing.postApi(
    endpoint: E,
    crossinline block: suspend RoutingContext.(Sent, E) -> Unit
) =
    post(endpoint.path) {
        val sentValue = call.receive<Sent>()
        block(sentValue, endpoint)
    }

fun <T> EndpointParam<T>.readFromCallOrNull(call: RoutingCall): T? {
    val str = call.request.queryParameters[this.key] ?: return null
    return this.read(str)
}

fun <T> EndpointParam<T>.readFromCall(call: RoutingCall): T =
    this.readFromCallOrNull(call) ?: throw MissingParameterException(this.key)

class MissingParameterException(val param: String) : Exception("Missing required parameter: $param")
