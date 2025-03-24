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
import newsref.model.dto.PrivateInfo
import newsref.model.dto.SourceBitDto
import newsref.model.dto.UserDto

object Api {
    object Login : GetEndpoint<AuthDto>("/login")

    object Articles : ParentEndpoint("/articles")
    object GetArticleById : GetByIdEndpoint<ArticleDto>(parent = Articles)

    object ChapterSources : GetEndpoint<ChapterSourceDto>("/chapter_sources") {
        val pageId = addLongParam("pageId")
        val chapterId = addLongParam("chapterId")
    }

    object Hosts : GetEndpoint<List<HostDto>>("/hosts") {
        val ids = addIntList("ids")
        val search = addStringParam("search")
    }

    object GetHostFeeds : GetEndpoint<List<FeedDto>>("/feeds", Hosts) {
        val core = addStringParam("core")
    }

    object GetHostById : GetByIdEndpoint<HostDto>(parent = Hosts)

    object GetHostSources : GetByIdEndpoint<List<SourceBitDto>>("/source", Hosts) {
        val start = addInstantParam("start")
    }

    object Huddles : ParentEndpoint("/huddles")
    object CreateHuddle : PostEndpoint<HuddleSeed, Long>(parent = Huddles)
    object ReadHuddlePrompt : PostEndpoint<HuddleKey, HuddlePrompt>("/prompt", Huddles)

    // user
    object GetUser : GetEndpoint<UserDto>("/user")
    object GetPrivateInfo : GetEndpoint<PrivateInfo>("/private", GetUser)

    object Chapters : GetEndpoint<List<ChapterPackDto>>("/chapters") {
        val start = addInstantParam("start")
    }

    object GetChapterById : GetByIdEndpoint<ChapterPackDto>(parent = Chapters)
}

val apiPrefixV1 = "/api/v1"