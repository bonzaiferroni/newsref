package streetlight.model

object Api {
    // utility
    val login = Endpoint("/login")

    // models
    val area = Endpoint("/data/area")
    val event = Endpoint("/data/event")
    val location = Endpoint("/data/location")
    val request = Endpoint("/data/request")

    // request
    val requestInfoEvent = Endpoint("/request_info/event")
    val requestInfo = Endpoint("/request_info")
    val requestInfoQueue = Endpoint("/request_info/queue")
    val requestInfoRandom = Endpoint("/request_info/random")

    // user
    val user = Endpoint("/user")
    val privateInfo = Endpoint("/user/private")
    val song = Endpoint("/user/song")
    val atlas = Endpoint("/user/atlas")

    // event
    val eventInfoCurrent = Endpoint("/event_info/current")
    val eventInfo = Endpoint("/event_info")
    val createEventRequest = Endpoint("/event_profile/request")
    val readEventRequests = Endpoint("/event_profile")
    val uploadEventImage = Endpoint("/event_profile/image")
}

val apiPrefix = "/api/v1"

data class Endpoint(
    val base: String
) {
    val path = "$apiPrefix$base"
    val clientIdTemplate: String get() = "$path/:id"
    val serverIdTemplate: String get() = "$path/{id}"
    fun replaceClientId(id: Int) = this.clientIdTemplate.replace(":id", id.toString())
}
