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
    object ChapterSources : Endpoint("/chapter_source") {
        val pageId = addLongParam("pageId")
        val chapterId = addLongParam("chapterId")
    }

    object Hosts : Endpoint("/host") {
        object Sources : Endpoint("/sources", this) {
            val start = addInstantParam("start")
        }
        object Feeds : Endpoint("/feeds", this) {
            val core = addStringParam("core")
        }
    }

    // user
    val userEndpoint = Endpoint("/user")
    val privateEndpoint = Endpoint("/user/private")

    object Chapters : Endpoint("/chapter") {
        val start = addInstantParam("start")
    }
}

val apiPrefix = "/api/v1"