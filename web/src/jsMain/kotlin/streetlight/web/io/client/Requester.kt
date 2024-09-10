package streetlight.web.io.client

import io.kvision.rest.*
import streetlight.web.apiAddress
import kotlin.js.Promise

interface Requester {
    var jwt: String?
    val restClient: RestClient
    val tokenHeaders: () -> List<Pair<String, String>>
}

class RequesterComponent(): Requester {
    override val restClient = RestClient()
    override var jwt: String? = null
    override val tokenHeaders = { listOf(Pair("Authorization", "Bearer $jwt")) }
}

inline fun <reified Returned : Any> Requester.request(
    method: HttpMethod,
    endpoint: String,
    crossinline block: RestRequestConfig<Returned, dynamic>.() -> Unit = {},
): Promise<RestResponse<Returned>> {
    return restClient.request("$apiAddress$endpoint") {
        applyConfigNoData(method, tokenHeaders)
        block()
    }
}

inline fun <reified Returned : Any, reified Sent : Any> Requester.request(
    method: HttpMethod,
    endpoint: String,
    data: Sent,
    crossinline block: RestRequestConfig<Returned, Sent>.() -> Unit = {},
): Promise<RestResponse<Returned>> {
    return restClient.request("${apiAddress}$endpoint", data) {
        applyConfig(method, tokenHeaders)
        block()
    }
}

inline fun Requester.requestDynamic(
    method: HttpMethod,
    endpoint: String,
    crossinline block: RestRequestConfig<dynamic, dynamic>.() -> Unit = {},
): Promise<RestResponse<dynamic>> {
    return restClient.requestDynamic("$apiAddress$endpoint") {
        applyConfigNoData(method, tokenHeaders)
        block()
    }
}

inline fun <reified Sent : Any> Requester.requestDynamic(
    method: HttpMethod,
    endpoint: String,
    data: Sent,
    crossinline block: RestRequestConfig<dynamic, Sent>.() -> Unit = {},
): Promise<RestResponse<dynamic>> {
    return restClient.requestDynamic<Sent>("${apiAddress}$endpoint", data) {
        applyConfig(method, tokenHeaders)
        block()
    }
}

inline fun <reified Sent: Any> Requester.requestText(
    method: HttpMethod,
    endpoint: String,
    data: Sent,
): Promise<RestResponse<String>> {
    return request<String, Sent>(method, endpoint, data) {
        responseBodyType = ResponseBodyType.TEXT
    }
}

inline fun <Returned : Any, reified Sent : Any> RestRequestConfig<Returned, Sent>.applyConfig(
    method: HttpMethod,
    noinline tokenHeaders: () -> List<Pair<String, String>>,
) {
    this.method = method
    this.headers = tokenHeaders
    this.serializer = getSerializer<Sent>()
}

fun <Returned : Any> RestRequestConfig<Returned, dynamic>.applyConfigNoData(
    method: HttpMethod,
    tokenHeaders: () -> List<Pair<String, String>>,
) {
    this.method = method
    this.headers = tokenHeaders
}