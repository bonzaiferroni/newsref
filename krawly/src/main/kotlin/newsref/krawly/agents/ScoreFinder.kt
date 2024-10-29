package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import newsref.db.globalConsole
import newsref.db.log.dim
import newsref.db.log.toGreen
import newsref.db.services.FeedSourceService
import newsref.db.services.ScoreService
import newsref.model.data.SourceScore
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit
import kotlin.time.measureTime

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
					console.logError("error finding scores\n${e}\n${e.stackTrace}")
				}
				console.logTrace("sleeping", "zz")
				delay(15.minutes)
			}
		}
	}

	private suspend fun findScores() {
		val now = Clock.System.now()
		val items = printTime("findNewLinksSince", console::log) {
			scoreService.findNewLinksSince(3.days)
		}

		val sourceIds = printTime("items map", console::log) { items.map { it.sourceId }.toSet() }
		val scores = printTime("scores map", console::log) {
			sourceIds.map { sourceId ->
				val score = items.filter { it.sourceId == sourceId }.map { it.hostId }.toSet().size
				SourceScore(sourceId, score, now)
			}
		}
		printTime("scoreService", console:: log) {
			scoreService.addScores(scores)
		}

		printTime("feedSourceService", console::log) {
			feedSourceService.addScores(scores)
		}

		val top = scores.takeIf{ it.isNotEmpty()}?.sortedByDescending { it.score }?.take(10)
		var msg = "looked at ${items.size} links, added ${scores.size} scores, top: ${top?.firstOrNull()?.score ?: 0}".toGreen()
		top?.forEach { (id, score) -> items.firstOrNull { it.sourceId == id }.let {
			msg += "\n${score.toString().padStart(4)}: ${it?.link?.url.toString().dim()}"
		}}
		console.log(msg)
	}
}


suspend fun <T> printTime(name: String, console: (String) -> Unit = { println(it) }, block: suspend () -> T): T {
	var value: T
	val duration = measureTime { value = block() }
	console("$name took ${duration.toString(DurationUnit.SECONDS)}")
	return value
}