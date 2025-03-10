package newsref.model

object Api {
    // utility
    val loginEndpoint = Endpoint("/login")

    // models
    // val area = Endpoint("/data/area")
    // val event = Endpoint("/data/event")
    // val location = Endpoint("/data/location")
    // val request = Endpoint("/data/request")

    // content
    val sourceEndpoint = Endpoint("/source")
    val feedEndpoint = Endpoint("/feed")
    val chapterEndpoint = Endpoint("/chapter")

    // user
    val userEndpoint = Endpoint("/user")
    val privateEndpoint = Endpoint("/user/private")
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