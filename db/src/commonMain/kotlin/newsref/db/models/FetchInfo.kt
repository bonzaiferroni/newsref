package newsref.db.models

import newsref.model.core.ArticleType
import newsref.model.core.CheckedUrl
import newsref.model.core.SourceType
import newsref.model.data.Article
import newsref.model.data.LeadInfo
import newsref.model.data.ResultType
import newsref.model.data.Source

data class FetchInfo(
    val id: Long = 0,
    val source: Source,
	val lead: LeadInfo,
	val page: PageInfo?,
	val resultType: ResultType
)

data class PageInfo(
	val article: Article,
	val articleType: ArticleType,
	val pageUrl: CheckedUrl,
	val type: SourceType,
	val hostId: Int,
	val hostName: String?,
	val language: String?,
	val foundNewsArticle: Boolean,
	val contents: Set<String>,
	val links: List<FetchLinkInfo>,
	val authors: Set<String>?,
	val junkParams: Set<String>?,
)

data class FetchLinkInfo(
    val url: CheckedUrl,
    val anchorText: String,
    val context: String?,
	val isExternal: Boolean,
)