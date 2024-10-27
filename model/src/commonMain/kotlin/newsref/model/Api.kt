package newsref.model

object Api {
    // utility
    val login = Endpoint("/login")

    // models
    // val area = Endpoint("/data/area")
    // val event = Endpoint("/data/event")
    // val location = Endpoint("/data/location")
    // val request = Endpoint("/data/request")

    // content
    val feedSource = Endpoint("/source/feed")

    // user
    val user = Endpoint("/user")
    val privateInfo = Endpoint("/user/private")

    // event
}

val apiPrefix = "/api/v1"

data class Endpoint(
    val base: String
) {
    val path = "$apiPrefix$base"
    val clientIdTemplate: String get() = "$path/:id"
    val serverIdTemplate: String get() = "$path/{id}"
    fun replaceClientId(id: Any) = this.clientIdTemplate.replace(":id", id.toString())
}
