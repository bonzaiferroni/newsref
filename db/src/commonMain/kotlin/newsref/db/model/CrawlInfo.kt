package newsref.db.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.model.data.ArticleCategory
import newsref.db.core.CheckedUrl
import newsref.db.core.LogBook
import newsref.model.data.CrawledAuthor
import kotlin.time.Duration.Companion.days

data class CrawlInfo(
	val crawledData: CrawledData?,
	val fetchResult: FetchResult,
	val fetch: FetchInfo,
	val cannonJunkParams: Set<String>?,
)

data class FetchInfo(
	val lead: LeadInfo,
	val leadHost: Host,
	val pastResults: List<LeadResult>,
	val logBook: LogBook,
	val skipFetch: Boolean = false,
	val result: WebResult? = null,
	val strategy: FetchStrategy? = null,
	val failedStrategy: FetchStrategy? = null,
	val navParams: Set<String>? = null,
	val junkParams: Set<String>? = null
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

data class CrawledData(
	val page: Page,
	val articleCategory: ArticleCategory = ArticleCategory.Unknown,
	val pageHost: Host,
	val hostName: String?,
	val language: String?,
	val foundNewsArticle: Boolean,
	val contents: Set<String>,
	val links: List<CrawledLink>,
	val authors: List<CrawledAuthor>?,
) {
	val isFresh get() = page.publishedAt.isFresh
}

data class CrawledLink(
    val url: CheckedUrl,
    val anchorText: String,
	val textIndex: Int,
    val context: String?,
	val isExternal: Boolean,
)

private val Instant?.isFresh get() = this == null || this > (Clock.System.now() - 30.days)