package newsref.server.utilities

import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import newsref.model.Endpoint
import newsref.model.EndpointParam
import newsref.server.extensions.getIdOrThrow

fun <T: Endpoint> Routing.getByPath(endpoint: T, block: suspend RoutingContext.(T) -> Unit) =
    get(endpoint.path) {
        block(endpoint)
    }

fun <T: Endpoint> Routing.getById(endpoint: T, block: suspend RoutingContext.(Long, T) -> Unit) =
    get(endpoint.serverIdTemplate) {
        val id = call.getIdOrThrow { it.toLongOrNull() }
        block(id, endpoint)
    }

fun <T> EndpointParam<T>.readFromCall(call: RoutingCall): T? {
    val str = call.request.queryParameters[this.key] ?: return null
    return this.read(str)
}