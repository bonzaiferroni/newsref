package newsref.krawly

import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.krawly.agents.FeedAgent
import newsref.krawly.agents.LeadAgent
import newsref.krawly.agents.OutletAgent
import newsref.krawly.agents.SourceAgent
import newsref.model.utils.getApexDomain
import kotlin.time.Duration.Companion.seconds

class Spider(
    private val web: SpiderWeb,
    private val outletAgent: OutletAgent = OutletAgent(web),
    private val feedAgent: FeedAgent = FeedAgent(web),
    private val leadAgent: LeadAgent = LeadAgent(web, outletAgent, feedAgent),
    private val sourceAgent: SourceAgent = SourceAgent(web, outletAgent)
) {
    private val attempts = mutableMapOf<String, Instant>()

    suspend fun startCrawling() {
        println("🕷🌄 waking up")

        val leads = leadAgent.getLeads()
        println("🕷👀 ${leads.size} initial leads")

        val deque = ArrayDeque(leads.shuffled())
        var previousApex: String? = null
        while (deque.size > 0) {
            // ensure good spider behavior
            val lead = deque.removeFirstOrNull() ?: break
            val apex = lead.url.getApexDomain()
            val lastAttempt = attempts[apex]
            val currentTime = Clock.System.now()
            if (lastAttempt != null && (currentTime - lastAttempt) < 30.seconds) {
                deque.addLast(lead)
                if(apex == previousApex) {
                    println("🕷💅 daydreaming")
                    delay((10000L..20000L).random())
                }
                continue
            }
            previousApex = apex
            attempts[apex] = currentTime

            println("🕷🕸 following: ${lead.url}")
            leadAgent.notifyAttempt(lead)
            val result = sourceAgent.follow(lead)
            if (result == null) {
                deque.addLast(lead)
                println("🕷💩 no result: ${lead.url}")
                continue
            }
            val sourceLeads = leadAgent.followUp(lead, result.sourceId, result.leads)
            println("🕷🤯 ${sourceLeads.size} new leads")
            sourceLeads.forEach { deque.addLast(it) }
        }

        println("🕷🛌 resting")
        delay((30000L..60000L).random())
        startCrawling()
    }
}

