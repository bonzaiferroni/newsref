package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import newsref.db.globalConsole
import newsref.db.log.toGreen
import newsref.db.services.FeedSourceService
import newsref.db.services.ScoreService
import newsref.model.data.SourceScore
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class ScoreFinder(
	private val scoreService: ScoreService = ScoreService(),
	private val feedSourceService: FeedSourceService = FeedSourceService(),
) {
	private val console = globalConsole.getHandle("ScoreFinder")

	fun start() {
		CoroutineScope(Dispatchers.Default).launch {
			while (true) {
				console.logTrace("finding scores", "🕷 ")
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
		val now = Clock.System.now()
		val items = scoreService.findNewLinksSince(3.days)
		val sourceIds = items.map { it.sourceId }.toSet()
		val scores = sourceIds.map { sourceId ->
			val score = items.filter { it.sourceId == sourceId }.map { it.hostId }.toSet().size
			SourceScore(sourceId, score, now)
		}.filter { it.score >= LINK_SCORE_MINIMUM }
		scoreService.addScores(scores)
		val top = scores.takeIf{ it.isNotEmpty()}?.sortedByDescending { it.score }?.take(10)
		console.log("looked at ${items.size} links, added ${scores.size} scores, top: ${top?.firstOrNull()?.score ?: 0}".toGreen())
		top?.forEach { (id, score) -> items.firstOrNull { it.sourceId == id }.let {
			console.log("${score}: ${it?.link?.url.toString()}")
		}}
	}
}

const val LINK_SCORE_MINIMUM = 5