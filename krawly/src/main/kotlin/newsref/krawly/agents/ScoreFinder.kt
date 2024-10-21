package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import newsref.db.globalConsole
import newsref.db.services.NexusService
import newsref.db.services.ScoreFinderService
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ScoreFinder(
	private val scoreFinderService: ScoreFinderService = ScoreFinderService(),
	private val nexusService: NexusService = NexusService(),
) {
	private val console = globalConsole.getHandle("ScoreFinder")

	fun start() {
		CoroutineScope(Dispatchers.Default).launch {
			while (true) {
				console.logTrace("finding scores", "🕷 ")
				findScores()
				console.logTrace("sleeping", "zz")
				delay((4..6).random().minutes)
			}
		}
	}

	private suspend fun findScores() {
		val items = scoreFinderService.findNewLinksSince(3.days)
		val targetSets = mutableMapOf<Long, MutableSet<String>>()
		for ((link, sourceCore) in items) {
			if (link.url.core.contains(".$sourceCore")) {
				console.log("Creating nexus: $sourceCore ❤ ${link.url.core}")
				nexusService.createNexus(sourceCore, link.url.core)
				scoreFinderService.setInternal(link.id)
				continue
			}
			val targetId = link.targetId ?: throw Exception("Target must not be null: ${link.url}")
			(targetSets.getOrPut(targetId) { mutableSetOf() }).add(link.url.core)
		}
		val scores = targetSets.filter { it.value.size > LINK_SCORE_MINIMUM }
			.map { Pair(it.key, it.value.size) }
		scoreFinderService.addScores(scores)
		val topScore = scores.takeIf{ it.isNotEmpty()}?.maxOf { it.second } ?: 0
		console.log("added ${scores.size} scores, top: $topScore")
	}
}

const val LINK_SCORE_MINIMUM = 5