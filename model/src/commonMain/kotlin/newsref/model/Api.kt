package newsref.model

import newsref.model.data.HuddleKey
import newsref.model.data.HuddlePrompt
import newsref.model.data.HuddleSeed
import newsref.model.dto.ArticleDto
import newsref.model.dto.AuthDto
import newsref.model.dto.ChapterPackDto
import newsref.model.dto.ChapterSourceDto
import newsref.model.dto.FeedDto
import newsref.model.dto.HostDto
import newsref.model.dto.HuddleContentDto
import newsref.model.dto.PrivateInfo
import newsref.model.dto.SourceBitDto
import newsref.model.dto.UserDto

object Api : ParentEndpoint(null, "/api/v1") {
    object Login : GetEndpoint<AuthDto>(this, "/login")

    object Articles : ParentEndpoint(this, "/articles") {
        object GetArticleById : GetByIdEndpoint<ArticleDto>(this)
    }

    object ChapterSources : GetEndpoint<ChapterSourceDto>(this, "/chapter_sources") {
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
        object GetHostSources : GetByIdEndpoint<List<SourceBitDto>>(this, "/source") {
            val start = addInstantParam("start")
        }
    }

    object Huddles : ParentEndpoint(this, "/huddles") {
        object CreateHuddle : PostEndpoint<HuddleSeed, Long>(this)
        object ReadHuddlePrompt : PostEndpoint<HuddleKey, HuddlePrompt>(this, "/prompt")
        object GetHuddleContentById : GetByIdEndpoint<HuddleContentDto>(this)
        object GetHuddleResponsesById : GetByIdEndpoint<HuddleContentDto>(this)
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
}
