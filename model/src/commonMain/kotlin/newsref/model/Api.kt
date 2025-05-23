package newsref.model

import kabinet.api.*
import kabinet.model.Auth
import kabinet.model.PrivateInfo
import kabinet.model.User
import newsref.model.data.*

object Api : ParentEndpoint(null, "/api/v1") {
    object Pages : ParentEndpoint(this, "/articles") {
        object GetArticleById : GetByIdEndpoint<Page>(this)
    }

    object Hosts : GetEndpoint<List<Host>>(this, "/hosts") {
        val ids = addIntList("ids")
        val search = addStringParam("search")

        object GetHostFeeds : GetEndpoint<List<Feed>>(this, "/feeds") {
            val core = addStringParam("core")
        }
        object GetHostById : GetByIdEndpoint<Host>(this)
        object GetHostPages : GetByIdEndpoint<List<PageLite>>(this, "/page") {
            val start = addInstantParam("start")
        }
    }

    object Huddles : ParentEndpoint(this, "/huddles") {
        object SubmitHuddleResponse : PostEndpoint<HuddleResponseSeed, HuddleResponseDto>(this)
        object ReadHuddlePrompt : PostEndpoint<HuddleKey, HuddlePrompt>(this, "/prompt")
        object GetHuddleContentById : GetByIdEndpoint<HuddleContentDto>(this, "/content")
        object GetHuddleResponsesById : GetByIdEndpoint<List<HuddleResponseDto>>(this, "/responses")
        object GetUserResponseId : GetByIdEndpoint<Long?>(this, "/response_id")
    }

    // user


    object Chapters : GetEndpoint<List<Chapter>>(this, "/chapters") {
        val start = addInstantParam("start")

        object GetChapterById : GetByIdEndpoint<Chapter>(this)

        object Pages : GetEndpoint<ChapterPage>(this, "/pages") {
            val pageId = addLongParam("pageId")
            val chapterId = addLongParam("chapterId")
        }

        object Persons : GetByIdEndpoint<List<ChapterPerson>>(this, "/persons")
    }

    object Logs : PostEndpoint<LogKey, List<Log>>(this, "/logs")
}
