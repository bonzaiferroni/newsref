package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import newsref.db.globalConsole
import newsref.db.log.dim
import newsref.db.log.toGreen
import newsref.db.services.ScoreService
import newsref.krawly.utils.profile
import newsref.model.data.SourceScore
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class ScoreFinder(
	private val scoreService: ScoreService = ScoreService(),
) {
	private val console = globalConsole.getHandle("ScoreFinder")

	fun start() {
		CoroutineScope(Dispatchers.Default).launch {
			while (true) {
				console.logTrace("finding scores", "🕷 ")
				try {
					findScores()
				} catch (e: Exception) {
					console.logError("error finding scores\n${e}\n${e.stackTrace}")
				}
				console.logTrace("sleeping", "zz")
				delay(15.minutes)
			}
		}
	}

	private suspend fun findScores() {
		val now = Clock.System.now()
		val items = scoreService.findNewLinksSince(3.days)
		val hostTally = mutableMapOf<Long, MutableSet<Int>>()
		for (item in items) {
			val set = hostTally.getOrPut(item.sourceId) { mutableSetOf() }
			set.add(item.hostId)
		}
		val scores = hostTally.map { (sourceId, set) -> SourceScore(sourceId, set.size, now) }

		scoreService.addScores(scores)

		val top = scores.takeIf{ it.isNotEmpty()}?.sortedByDescending { it.score }?.take(10)
		var msg = "looked at ${items.size} links, added ${scores.size} scores, top: ${top?.firstOrNull()?.score ?: 0}".toGreen()
		top?.forEach { (id, score) -> items.firstOrNull { it.sourceId == id }.let {
			msg += "\n${score.toString().padStart(4)}: ${it?.link?.url.toString().dim()}"
		}}
		console.log(msg)
	}
}
