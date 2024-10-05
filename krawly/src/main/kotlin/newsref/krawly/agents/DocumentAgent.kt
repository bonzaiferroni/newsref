package newsref.krawly.agents

import it.skrape.selects.Doc
import newsref.db.globalConsole
import newsref.db.services.ArticleService
import newsref.db.utils.cacheResource
import newsref.krawly.MAX_URL_ATTEMPTS
import newsref.krawly.SpiderWeb
import newsref.krawly.utils.*
import newsref.model.core.toCheckedOrNull
import newsref.model.core.toUrlOrNull
import newsref.model.data.Lead
import newsref.model.data.LeadJob
import newsref.model.data.Outlet
import newsref.model.dto.DocumentInfo

class DocumentAgent(
	private val outletAgent: OutletAgent,
	// private val articleService: ArticleService = ArticleService()
) {
	private val console = globalConsole.getHandle("DocumentAgent")

	suspend fun readDoc(job: LeadJob, outlet: Outlet, doc: Doc): DocumentInfo? {
		val newsArticle = doc.getNewsArticle(job.url)
		console.logInfo("DocumentAgent", "NewsArticle ${job.url.host}: ${if (newsArticle != null) "??" else "null"}")
		val sourceUrl = (newsArticle?.url ?: doc.readUrl())?.toUrlOrNull() ?: job.url
		val sourceOutlet = outletAgent.getOutlet(sourceUrl)                     // <- OutletAgent ->

		val sourceCheckedUrl = sourceUrl.toString().toCheckedOrNull(outlet) ?: return null

		val docInfo = doc.read(sourceCheckedUrl, sourceOutlet, newsArticle)     // <- read document

		if (docInfo.contents.isNotEmpty()) {
			val md = docInfo.toMarkdown()
			md.cacheResource(job.url, "md")
		}

		return docInfo
	}
}