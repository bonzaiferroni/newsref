package newsref.krawly.agents

import it.skrape.selects.Doc
import newsref.db.services.ArticleService
import newsref.krawly.utils.*
import newsref.model.data.Lead
import newsref.model.data.Outlet
import newsref.model.dto.DocumentInfo
import newsref.model.utils.tryParseTrustedUrl

class DocumentAgent(
	private val outletAgent: OutletAgent,
	private val articleService: ArticleService = ArticleService()
) {
	suspend fun readDoc(lead: Lead, doc: Doc, outlet: Outlet): DocumentInfo {
		val newsArticle = doc.getNewsArticle(lead.url)
			.also { if (it != null) print("📜 ") }
		val sourceUrl = newsArticle?.url?.tryParseTrustedUrl(outlet.urlParams, null)
			?: doc.readUrl()?.tryParseTrustedUrl(outlet.urlParams, null) ?: lead.url
		val outletName = newsArticle?.publisher?.name
			?: doc.readOutletName()

		val sourceOutlet = outletAgent.findAndSetName(sourceUrl, outletName)    // <- OutletAgent ->
		val docInfo = doc.read(sourceUrl, sourceOutlet, newsArticle)            // <- read document

		return docInfo
	}
}