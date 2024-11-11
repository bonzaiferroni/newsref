package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import newsref.db.globalConsole
import newsref.db.log.dim
import newsref.db.log.toGreen
import newsref.db.services.LinkTracer
import newsref.db.services.ScoreService
import newsref.db.services.SourceTracer
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
		val linkTracers = scoreService.findNewLinksSince(30.days)
		val hostTally = mutableMapOf<Long, MutableList<LinkTracer>>()
		for (tracer in linkTracers) {
			val set = hostTally.getOrPut(tracer.sourceId) { mutableListOf() }
			set.add(tracer)
		}
		val sourceTracers = hostTally.map { (sourceId, links) ->
			val hostIdSet = mutableSetOf<Int>()
			val scores = mutableListOf<SourceScore>()
			var score = 0
			for (tracer in links.sortedBy { it.linkedAt }) {
				if (hostIdSet.contains(tracer.hostId)) continue
				hostIdSet.add(tracer.hostId)
				score++
				scores.add(SourceScore(sourceId, tracer.link.id, score, tracer.linkedAt))
			}
			SourceTracer(sourceId, score, scores)
		}

		scoreService.addScores(sourceTracers)

		val top = sourceTracers.takeIf{ it.isNotEmpty()}?.sortedByDescending { it.score }?.take(10)
		var msg = "looked at ${linkTracers.size} links, added ${sourceTracers.size} scores, top: ${top?.firstOrNull()?.score ?: 0}".toGreen()
		top?.forEach { (id, score) -> linkTracers.firstOrNull { it.sourceId == id }.let {
			msg += "\n${score.toString().padStart(4)}: ${it?.link?.url.toString().dim()}"
		}}
		console.log(msg)
	}
}
