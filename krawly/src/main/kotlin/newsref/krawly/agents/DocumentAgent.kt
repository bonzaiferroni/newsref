package newsref.krawly.agents

import newsref.db.utils.cacheResource
import newsref.krawly.MAX_URL_ATTEMPTS
import newsref.krawly.SpiderWeb
import newsref.krawly.utils.*
import newsref.model.core.toCheckedOrNull
import newsref.model.core.toUrlOrNull
import newsref.model.data.Lead
import newsref.model.data.Outlet
import newsref.model.dto.DocumentInfo

class DocumentAgent(
	private val web: SpiderWeb,
	private val outletAgent: OutletAgent,
	// private val articleService: ArticleService = ArticleService()
) {
	suspend fun readDoc(lead: Lead, outlet: Outlet): DocumentInfo? {
		val result = web.crawlPage(lead.url, true) ?: return null               // <- Web

		if (!result.isSuccess() && lead.attemptCount < MAX_URL_ATTEMPTS) {
			result.screenshot?.cacheResource(lead.url, "png", "nav_fail")
			return null
		}
		result.screenshot?.cacheResource(lead.url, "png")
		result.doc?.html?.cacheResource(lead.url, "html", "content")

		val doc = result.doc ?: return null

		val newsArticle = doc.getNewsArticle(lead.url)
			.also { if (it != null) print("📜 ") }
		val sourceUrl = (newsArticle?.url ?: doc.readUrl())?.toUrlOrNull() ?: lead.url
		val sourceOutlet = outletAgent.getOutlet(sourceUrl)      				// <- OutletAgent ->

		val sourceCheckedUrl = sourceUrl.toString().toCheckedOrNull(outlet) ?: return null

		val docInfo = doc.read(sourceCheckedUrl, sourceOutlet, newsArticle)     // <- read document

		if (docInfo.contents.isNotEmpty()) {
			val md = docInfo.toMarkdown()
			md.cacheResource(lead.url, "md")
		}

		return docInfo
	}
}