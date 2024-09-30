package newsref.krawly.agents

import newsref.db.services.LeadService
import newsref.krawly.SpiderWeb
import newsref.model.data.Lead


class LeadAgent(
    private val web: SpiderWeb,
    private val outletAgent: OutletAgent,
    private val feedAgent: FeedAgent,
    private val leadService: LeadService = LeadService()
) {

    suspend fun getLeads(): List<Lead> {
        val leads = mutableListOf<Lead>()
        val unfollowed = leadService.getUnfollowed()
        val feedLeads = feedAgent.checkFeeds()
        for (feedLead in feedLeads) {
            if (!outletAgent.isAllowed(feedLead.url)) continue
            val lead = leadService.createIfFresh(feedLead.url, feedLead.feedId, feedLead.headline) ?: continue
            leads += lead
        }
        leads += unfollowed
        return leads
    }

    suspend fun notifyAttempt(lead: Lead) {
        leadService.addAttempt(lead)
    }

    suspend fun followUp(origin: Lead, sourceId: Long, newLeads: List<String>): List<Lead> {
        val leads = mutableListOf<Lead>()
        leadService.addSource(origin, sourceId)
        for (url in newLeads) {
            if (!outletAgent.isAllowed(url)) continue
            val lead = leadService.createIfFresh(url) ?: continue
            leads += lead
        }
        return leads
    }

}