package newsref.model

import newsref.model.data.HuddleKey
import newsref.model.data.HuddlePrompt
import newsref.model.data.HuddleResponseSeed
import newsref.model.data.Page
import newsref.model.dto.PageDto
import newsref.model.dto.AuthDto
import newsref.model.dto.ChapterPackDto
import newsref.model.dto.ChapterPageDto
import newsref.model.dto.FeedDto
import newsref.model.dto.HostDto
import newsref.model.dto.HuddleContentDto
import newsref.model.dto.HuddleResponseDto
import newsref.model.dto.LogDto
import newsref.model.dto.LogKey
import newsref.model.dto.PrivateInfo
import newsref.model.dto.PageBitDto
import newsref.model.dto.UserDto

object Api : ParentEndpoint(null, "/api/v1") {
    object Login : GetEndpoint<AuthDto>(this, "/login")

    object Pages : ParentEndpoint(this, "/articles") {
        object GetArticleById : GetByIdEndpoint<Page>(this)
    }

    object ChapterPages : GetEndpoint<ChapterPageDto>(this, "/chapter_pages") {
        val pageId = addLongParam("pageId")
        val chapterId = addLongParam("chapterId")
    }

    object Hosts : GetEndpoint<List<HostDto>>(this, "/hosts") {
        val ids = addIntList("ids")
        val search = addStringParam("search")

        object GetHostFeeds : GetEndpoint<List<FeedDto>>(this, "/feeds") {
            val core = addStringParam("core")
        }
        object GetHostById : GetByIdEndpoint<HostDto>(this)
        object GetHostPages : GetByIdEndpoint<List<PageBitDto>>(this, "/page") {
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
        object GetUser : GetEndpoint<UserDto>(this)
        object GetPrivateInfo : GetEndpoint<PrivateInfo>(this, "/private")
    }

    object Chapters : GetEndpoint<List<ChapterPackDto>>(this, "/chapters") {
        val start = addInstantParam("start")

        object GetChapterById : GetByIdEndpoint<ChapterPackDto>(this)
    }

    object Logs : PostEndpoint<LogKey, List<LogDto>>(this, "/logs")
}
