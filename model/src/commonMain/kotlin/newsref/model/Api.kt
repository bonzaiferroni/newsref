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
    object ChapterSourceEndpoint : Endpoint("/chapter_source") {
        val pageId = addLongParam("pageId")
        val chapterId = addLongParam("chapterId")
    }

    // user
    val userEndpoint = Endpoint("/user")
    val privateEndpoint = Endpoint("/user/private")

    object ChapterEndpoint : Endpoint("/chapter") {
        val start = addInstantParam("start")
    }
}

val apiPrefix = "/api/v1"