package newsref.krawly.agents

import kotlinx.coroutines.*
import newsref.db.*
import newsref.db.console.dim
import newsref.db.console.toGreen
import newsref.db.model.SourceScore
import newsref.db.services.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

private val console = globalConsole.getHandle("SourceScoreFinder")

class SourceScoreFinder(
	private val sourceScoreService: SourceScoreService = SourceScoreService(),
) {
	fun start() {
		CoroutineScope(Dispatchers.Default).launch {
			while (true) {
				console.logTrace("finding source scores", "🕷 ")
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
		val signals = sourceScoreService.findScoreSignals(30.days)
		console.log("found ${signals.size} score signals")

		val calculatedScores = signals.groupBy { it.targetId }.map { (targetId, list) ->
			val scores = mutableListOf<SourceScore>()
			val lowestFeedPosition = list.filter { it.feedPosition != null }
				.sortedBy { it.feedPosition }
				.firstOrNull()

			var score = if (lowestFeedPosition != null) {
				val feedScore = (MAX_FEED_SIGNAL - lowestFeedPosition.feedPosition!!)
					.coerceIn(0..MAX_FEED_SIGNAL)
				scores.add(
                    SourceScore(
                        targetId,
                        feedScore,
                        lowestFeedPosition.linkedAt,
                        null,
                        lowestFeedPosition.feedId
                    )
                )
				feedScore
			} else { 0 }

			val topSignals = list.filter { it.originHostId != null }
				.groupBy { it.originHostId }.values.map {
					it.sortedWith (
						compareByDescending<ScoreSignal> { it.originScore ?: 0 }
							.thenBy { it.linkedAt }
					).first()
				}
				.sortedBy { it.linkedAt }

			for (signal in topSignals) {
				val signalScore = (signal.originScore ?: 1).coerceIn(1..MAX_LINK_SIGNAL)
				score += signalScore
				scores.add(SourceScore(targetId, score, signal.linkedAt, signal.originId, null))
			}

			CalculatedScore(targetId, score, scores)
		}

		sourceScoreService.addScores(calculatedScores)

		val top = calculatedScores.takeIf{ it.isNotEmpty()}?.sortedByDescending { it.score }?.take(10)
		var msg = "looked at ${signals.size} links, added ${calculatedScores.size} scores, top: ${top?.firstOrNull()?.score ?: 0}".toGreen()
		top?.forEach { (id, score) -> signals.firstOrNull { it.targetId == id }.let {
			msg += "\n${score.toString().padStart(4)}: ${it?.url.toString().dim()}"
		}}
		console.log(msg)
	}
}