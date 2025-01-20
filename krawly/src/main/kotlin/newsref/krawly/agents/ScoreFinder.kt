package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import newsref.db.globalConsole
import newsref.db.log.dim
import newsref.db.log.toGreen
import newsref.db.services.CalculatedScore
import newsref.db.services.ScoreService
import newsref.db.utils.profile
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
		val signals = profile("find signals", true) { scoreService.findScoreSignals(30.days) }
		console.log("found ${signals.size} score signals")

		val sourceTracers = profile("calculate scores", true) {
			signals.groupBy { it.targetId }.map {
				val linkScore = it.value.filter { it.originHostId != null }
					.groupBy { it.originHostId }.values
					.sumOf { it.maxOf { it.originScore?.coerceIn(0..maxLinkSignal) ?: 1 } }
				val lowestFeedPosition = it.value.filter { it.feedPosition != null }
					.sortedBy { it.feedPosition }
					.firstOrNull()

				val feedScore = lowestFeedPosition?.let { maxFeedSignal - it.feedPosition!! }
					?.coerceIn(0..maxFeedSignal)
					?: 0

				CalculatedScore(it.key, linkScore + feedScore)
			}
		}

		scoreService.addScores(sourceTracers)

		val top = sourceTracers.takeIf{ it.isNotEmpty()}?.sortedByDescending { it.score }?.take(10)
		var msg = "looked at ${signals.size} links, added ${sourceTracers.size} scores, top: ${top?.firstOrNull()?.score ?: 0}".toGreen()
		top?.forEach { (id, score) -> signals.firstOrNull { it.targetId == id }.let {
			msg += "\n${score.toString().padStart(4)}: ${it?.url.toString().dim()}"
		}}
		console.log(msg)
	}
}

val maxLinkSignal = 3
val maxFeedSignal = 3