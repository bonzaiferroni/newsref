package newsref.model.dto

import newsref.model.core.CheckedUrl
import newsref.model.core.SourceType
import newsref.model.data.Article
import newsref.model.data.LeadInfo
import newsref.model.data.Source

data class FetchInfo(
    val id: Long = 0,
    val source: Source,
	val lead: LeadInfo,
	val page: PageInfo?
)

data class PageInfo(
	val article: Article,
	val pageUrl: CheckedUrl,
	val type: SourceType,
	val hostId: Int,
	val hostName: String?,
	val contents: Set<String>,
	val links: List<LinkInfo>,
	val authors: Set<String>?,
	val junkParams: Set<String>?,
)

data class LinkInfo(
    val url: CheckedUrl,
    val anchorText: String,
    val context: String,
)