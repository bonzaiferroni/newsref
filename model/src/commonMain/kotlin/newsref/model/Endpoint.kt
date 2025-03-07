package newsref.model

import kotlinx.datetime.Instant

open class Endpoint(
    val base: String,
    val parent: Endpoint? = null,
) {
    val baseWithParent: String = when {
        parent != null -> "${parent.baseWithParent}$base"
        else -> base
    }

    val path = "$apiPrefix$baseWithParent"
    val clientIdTemplate: String get() = "$path/:id"
    val serverIdTemplate: String get() = "$path/{id}"
    fun replaceClientId(id: Any) = this.clientIdTemplate.replace(":id", id.toString())
    fun <T> addParam(key: String, toValue: (String) -> T, toString: (T) -> String, ) =
        EndpointParam<T>(key, toValue, toString)

    fun addLongParam(key: String) = EndpointParam(
        key = key,
        toValue = { it.toLong() },
        toString = { it.toString() }
    )

    fun addInstantParam(key: String) = EndpointParam(
        key = key,
        toValue = { Instant.fromEpochSeconds(it.toLong())},
        toString = { it.epochSeconds.toString() }
    )
}

class EndpointParam<T>(
    val key: String,
    private val toValue: (String) -> T,
    private val toString: (T) -> String,
) {
    fun write(value: T) = key to toString(value)
    fun read(str: String) = toValue(str)
}