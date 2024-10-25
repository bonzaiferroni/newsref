package newsref.db.models

import newsref.model.core.ArticleType
import newsref.model.core.CheckedUrl
import newsref.model.core.SourceType
import newsref.model.data.*

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
	val screenshot: ByteArray? = null,
	val timeout: Boolean = false,
	val exception: String? = null
) {
	val isOk get() = status in 200..299
	val isNotFound get() = status == 404
}

data class PageInfo(
	val article: Article,
	val articleType: ArticleType,
	val pageUrl: CheckedUrl,
	val pageHost: Host,
	val pageTitle: String,
	val type: SourceType,
	val hostName: String?,
	val language: String?,
	val foundNewsArticle: Boolean,
	val contentWordCount: Int,
	val contents: Set<String>,
	val links: List<PageLink>,
	val authors: Set<String>?,
)

data class PageLink(
    val url: CheckedUrl,
    val anchorText: String,
    val context: String?,
	val isExternal: Boolean,
)