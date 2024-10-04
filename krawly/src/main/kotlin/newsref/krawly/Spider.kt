package newsref.krawly

import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.log.LogConsole
import newsref.db.log.LogTable
import newsref.krawly.agents.FeedAgent
import newsref.krawly.agents.LeadAgent
import newsref.krawly.agents.OutletAgent
import newsref.krawly.agents.SourceAgent
import kotlin.time.Duration.Companion.seconds

class Spider(
    private val web: SpiderWeb,
    private val console: LogConsole = LogConsole(),
    private val outletAgent: OutletAgent = OutletAgent(web),
    private val feedAgent: FeedAgent = FeedAgent(web, outletAgent),
    private val leadAgent: LeadAgent = LeadAgent(web, feedAgent),
    private val sourceAgent: SourceAgent = SourceAgent(web, outletAgent)
) {
    private val attempts = mutableMapOf<String, Instant>()

    suspend fun startCrawling() {
        console.log("waking up 🌄")

        val leads = leadAgent.getLeads()                                        // <- LeadAgent ->

        console.log("${leads.size} initial leads")

        val deque = ArrayDeque(leads.shuffled())
        var previousHost: String? = null
        while (deque.size > 0) {
            // ensure good spider behavior
            val lead = deque.removeFirstOrNull() ?: break
            val host = lead.url.host
            val lastAttempt = attempts[host]
            val currentTime = Clock.System.now()
            if (lastAttempt != null && (currentTime - lastAttempt) < 30.seconds) {
                deque.addLast(lead)
                if(host == previousHost) {
                    console.log("🕷💅 daydreaming")
                    delay((5000L..10000L).random())
                }
                continue
            }
            previousHost = host
            attempts[host] = currentTime

            console.log("🕷🕸 following: ${lead.url}")
            leadAgent.notifyAttempt(lead)                                       //    LeadAgent ->
            val result = sourceAgent.followLead(lead)                           // <- SourceAgent ->
            if (result == null) {
                deque.addLast(lead)
                console.log("🕷💩 no result: ${lead.url}")
                continue
            }
            val sourceLeads = leadAgent.followUp(lead, result)                  // <- LeadAgent ->
                ?: continue
            console.log("🕷🤯 ${sourceLeads.size} new leads")
            sourceLeads.forEach { deque.addLast(it) }
        }

        console.log("🕷🛌 resting")
        delay((30000L..60000L).random())
        startCrawling()
    }
}

