package newsref.model

import newsref.model.data.HuddleKey
import newsref.model.data.HuddleSeed

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
    object TopSources : Endpoint("/top_chapters") {
        val spanOrdinal = addIntParam("span_ordinal")
    }
    object ChapterSources : Endpoint("/chapter_source") {
        val pageId = addLongParam("pageId")
        val chapterId = addLongParam("chapterId")
    }

    object Hosts : Endpoint("/host") {
        val ids = addIntList("ids")
        val search = addStringParam("search")

        object Sources : Endpoint("/sources", this) {
            val start = addInstantParam("start")
        }
        object Feeds : Endpoint("/feeds", this) {
            val core = addStringParam("core")
        }
    }

    object Huddles : PostEndpoint<HuddleSeed>("/huddle") {
        object Options : PostEndpoint<HuddleKey>("/options", this)
    }

    // user
    val userEndpoint = Endpoint("/user")
    val privateEndpoint = Endpoint("/user/private")

    object Chapters : Endpoint("/chapter") {
        val start = addInstantParam("start")
    }
}

val apiPrefix = "/api/v1"