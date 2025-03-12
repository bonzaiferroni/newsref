package newsref.db.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.model.core.ArticleType
import newsref.db.core.CheckedUrl
import newsref.model.dto.PageAuthor
import kotlin.time.Duration.Companion.days

data class CrawlInfo(
	val page: PageInfo?,
	val fetchResult: FetchResult,
	val fetch: FetchInfo,
	val cannonJunkParams: Set<String>?
)

data class FetchInfo(
	val lead: LeadInfo,
	val leadHost: Host,
	val pastResults: List<LeadResult>,
	val skipFetch: Boolean = false,
	val result: WebResult? = null,
	val strategy: FetchStrategy? = null,
	val failedStrategy: FetchStrategy? = null,
	val navParams: Set<String>? = null,
	val junkParams: Set<String>? = null,
)

data class WebResult(
	val pageHref: String? = null,
	val status: Int? = null,
	val content: String? = null,
	val timeout: Boolean = false,
	val exception: String? = null,
	val noConnection: Boolean = false,
) {
	val isOk get() = status in 200..299
	val isNotFound get() = status == 404
}

data class PageInfo(
	val source: Source,
	val article: Article? = null,
	val articleType: ArticleType = ArticleType.UNKNOWN,
	val pageHost: Host,
	val hostName: String?,
	val language: String?,
	val foundNewsArticle: Boolean,
	val contents: Set<String>,
	val links: List<PageLink>,
	val authors: List<PageAuthor>?,
) {
	val isFresh get() = source.publishedAt.isFresh
}

data class PageLink(
    val url: CheckedUrl,
    val anchorText: String,
	val textIndex: Int,
    val context: String?,
	val isExternal: Boolean,
)

private val Instant?.isFresh get() = this == null || this > (Clock.System.now() - 30.days)