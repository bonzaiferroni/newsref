package newsref.krawly.agents

import kotlinx.datetime.Clock
import newsref.db.globalConsole
import newsref.db.services.LeadService
import newsref.db.console.toPink
import newsref.db.console.toYellow
import newsref.db.core.CheckedUrl
import newsref.db.model.CrawlInfo
import newsref.db.services.CreateLeadResult
import newsref.krawly.utils.TallyMap
import newsref.krawly.utils.increment
import newsref.db.model.LeadJob
import newsref.db.model.FetchResult
import kotlin.time.Duration.Companion.days

class LeadMaker(
	private val leadService: LeadService = LeadService(),
) {
	private val console = globalConsole.getHandle("LeadMaker")

	suspend fun makeLead(checkedUrl: CheckedUrl, leadJob: LeadJob? = null, createIfFresh: Boolean): CreateLeadResult {
		return try {
			leadService.createOrLinkLead(checkedUrl, leadJob, createIfFresh)
		} catch (e: IllegalArgumentException) {
			val urlString = checkedUrl.toString().toYellow()
			console.logWarning(e.message?.let {
				"Error creating job: ${checkedUrl.core}\n${it.toPink()}"
			} ?: "Error creating job: $urlString")
			CreateLeadResult.ERROR // return
		}
	}

	suspend fun makeCrawlLeads(crawl: CrawlInfo): TallyMap<CreateLeadResult> {
		val resultMap = mutableMapOf<CreateLeadResult, Int>()
		val page = crawl.crawledData ?: return resultMap
		val links = crawl.crawledData?.links ?: return resultMap
		for (link in links) {
			val publishedAt = page.page.publishedAt
			val freshSource = publishedAt != null && publishedAt > (Clock.System.now() - 30.days)
			val createIfFresh = crawl.fetchResult == FetchResult.RELEVANT && freshSource
			if (!createIfFresh && !link.isExternal) continue
			val job = LeadJob(
				isExternal = link.isExternal,
				freshAt = crawl.crawledData?.page?.publishedAt
					?: crawl.crawledData?.page?.seenAt
					?: crawl.fetch.lead.freshAt
			)
			val result = makeLead(link.url, job, createIfFresh)
			if (result == CreateLeadResult.AFFIRMED)
				console.log("affirmed")
			resultMap.increment(result)
		}
		return resultMap
	}
}