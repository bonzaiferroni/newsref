package newsref.model

import kotlinx.datetime.Instant

sealed class Endpoint<Received>(
    val parent: Endpoint<*>?,
    val pathNode: String
) {
    val path: String = parent?.let { "${it.path}$pathNode" } ?: pathNode

    fun <T> addParam(key: String, toValue: (String) -> T, toString: (T) -> String, ) =
        EndpointParam<T>(key, toValue, toString)

    fun addLongParam(key: String) = EndpointParam(
        key = key,
        toValue = { it.toLong() },
        toString = { it.toString() }
    )

    fun addIntParam(key: String) = EndpointParam(
        key = key,
        toValue = { it.toInt() },
        toString = { it.toString() }
    )

    fun addInstantParam(key: String) = EndpointParam(
        key = key,
        toValue = { Instant.fromEpochSeconds(it.toLong())},
        toString = { it.epochSeconds.toString() }
    )

    fun addStringParam(key: String) = EndpointParam(
        key = key,
        toValue = { it },
        toString = { it }
    )

    fun addIntList(key: String) = addListParam(key, { it.toInt()}, { it.toString() })

    fun <T> addListParam(key: String, toValue: (String) -> T, toString: (T) -> String) = EndpointParam<Collection<T>>(
        key = key,
        toValue = { it.takeIf { it.isNotEmpty() }?. split(",")?.map { toValue(it)} ?: emptyList() },
        toString = { it.joinToString(",") { toString(it)} }
    )
}

open class ParentEndpoint(
    parent: Endpoint<*>? = null,
    pathNode: String,
): Endpoint<Unit>(parent, pathNode)

open class GetEndpoint<Returned>(
    parent: Endpoint<*>? = null,
    pathNode: String = "",
): Endpoint<Returned>(parent, pathNode)

open class PostEndpoint<Sent, Returned>(
    parent: Endpoint<*>? = null,
    pathNode: String = "",
): Endpoint<Returned>(parent, pathNode)

open class GetByIdEndpoint<Returned>(
    parent: Endpoint<*>? = null,
    pathNode: String = "",
) : Endpoint<Returned>(parent, pathNode) {
    val clientIdTemplate: String get() = "$path/:id"
    val serverIdTemplate: String get() = "$path/{id}"
    fun replaceClientId(id: Any) = this.clientIdTemplate.replace(":id", id.toString())
}

class EndpointParam<T>(
    val key: String,
    private val toValue: (String) -> T,
    private val toString: (T) -> String,
) {
    fun write(value: T) = key to toString(value)
    fun read(str: String) = toValue(str)
}

class ApiRequestBuilder<E: Endpoint<*>>(
    val endpoint: E
) {
    var params: List<Pair<String, String>>? = null

    fun setParams(vararg params: Pair<String, String>) {
        this.params = params.asList()
    }
}