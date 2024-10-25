package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import newsref.db.globalConsole
import newsref.db.services.NexusService
import newsref.db.services.ScoreFinderService
import newsref.db.services.ScoreGroup
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class ScoreFinder(
	private val scoreFinderService: ScoreFinderService = ScoreFinderService(),
) {
	private val console = globalConsole.getHandle("ScoreFinder")

	fun start() {
		CoroutineScope(Dispatchers.Default).launch {
			while (true) {
				console.logDebug("finding scores", "🕷 ")
				try {
					findScores()
				} catch (e: Exception) {
					console.logError("error finding scores", e)
				}
				console.logTrace("sleeping", "zz")
				delay((4..6).random().minutes)
			}
		}
	}

	private suspend fun findScores() {
		val items = scoreFinderService.findNewLinksSince(3.days)
		val sourceIds = items.map { it.sourceId }.toSet()
		val scores = sourceIds.map { sourceId ->
			val score = items.filter { it.sourceId == sourceId }.map { it.hostId }.toSet().size
			ScoreGroup(sourceId, score)
		}.filter { it.count >= LINK_SCORE_MINIMUM }
		scoreFinderService.addScores(scores)
		val top = scores.takeIf{ it.isNotEmpty()}?.sortedByDescending { it.count }?.take(5)
		console.log("looked at ${items.size} links, added ${scores.size} scores, top: ${top?.firstOrNull()?.count ?: 0}")
		top?.forEach { (id, score) -> items.firstOrNull { it.sourceId == id }.let {
			console.log("${score}: ${it?.link?.url.toString()}")
		}}
	}
}

const val LINK_SCORE_MINIMUM = 5