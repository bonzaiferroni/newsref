package newsref.krawly.agents

import newsref.db.services.LeadService
import newsref.krawly.SpiderWeb
import newsref.model.data.Lead
import newsref.model.data.SourceType
import newsref.model.dto.SourceInfo


class LeadAgent(
    private val web: SpiderWeb,
    private val feedAgent: FeedAgent,
    private val leadService: LeadService = LeadService()
) {

    suspend fun getLeads(): List<Lead> {
        val leads = mutableListOf<Lead>()
        val unfollowed = leadService.getUnfollowed()                            // <- LeadService
        val feedLeads = feedAgent.checkFeeds()                                  // <- FeedAgent ->
        for (feedLead in feedLeads) {
            val lead = leadService.createIfFresh(                               //    LeadService ->
                feedLead.url,
                feedLead.feedId,
                feedLead.headline
            ) ?: continue
            leads += lead
        }
        leads += unfollowed
        return leads
    }

    suspend fun notifyAttempt(lead: Lead) {
        // leadService.addAttempt(lead)                                            //    LeadService ->
    }

    suspend fun followUp(origin: Lead, sourceInfo: SourceInfo): List<Lead>? {
        val leads = mutableListOf<Lead>()
        leadService.addSource(origin, sourceInfo.id)                            //    LeadService ->
        if (sourceInfo.source.type != SourceType.ARTICLE) return null
        val newLeads = sourceInfo.document?.links?.map { it.url }
            ?: return null
        for (url in newLeads) {
            val lead = leadService.createIfFresh(url) ?: continue               //    LeadService ->
            leads += lead
        }
        return leads
    }

}