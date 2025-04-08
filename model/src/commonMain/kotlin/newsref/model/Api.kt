package newsref.model

import newsref.model.data.*

object Api : ParentEndpoint(null, "/api/v1") {
    object Login : GetEndpoint<Auth>(this, "/login")

    object Pages : ParentEndpoint(this, "/articles") {
        object GetArticleById : GetByIdEndpoint<Page>(this)
    }

    object ChapterPages : GetEndpoint<ChapterPage>(this, "/chapter_pages") {
        val pageId = addLongParam("pageId")
        val chapterId = addLongParam("chapterId")
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
    object Users : ParentEndpoint(this, "/users") {
        object GetUser : GetEndpoint<User>(this)
        object GetPrivateInfo : GetEndpoint<PrivateInfo>(this, "/private")
    }

    object Chapters : GetEndpoint<List<Chapter>>(this, "/chapters") {
        val start = addInstantParam("start")

        object GetChapterById : GetByIdEndpoint<Chapter>(this)
    }

    object Logs : PostEndpoint<LogKey, List<Log>>(this, "/logs")
}
