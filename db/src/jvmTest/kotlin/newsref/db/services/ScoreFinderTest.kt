package newsref.db.services

import kotlinx.datetime.Clock
import newsref.db.DbTest
import newsref.model.data.SourceScore
import kotlin.test.Test
import kotlin.time.Duration.Companion.days

class ScoreFinderTest : DbTest(true) {

	@Test
	fun `explore code`(): Unit = dbQuery {
		val now = Clock.System.now()
		val scoreService = ScoreService()
		val items = scoreService.findNewLinksSince(3.days)
		val sourceIds = items.map { it.sourceId }.toSet()
		val scores = sourceIds.map { sourceId ->
			val score = items.filter { it.sourceId == sourceId }.map { it.hostId }.toSet().size
			SourceScore(sourceId, score, now)
		}
		val top = scores.takeIf{ it.isNotEmpty()}?.sortedByDescending { it.score }?.take(5)
		println("looked at ${items.size} links, added ${scores.size} scores, top: ${top?.firstOrNull()?.score ?: 0}")
		top?.forEach { (id, score) -> items.firstOrNull { it.sourceId == id }.let {
			println("${score}: ${it?.link?.url.toString()}")
		}}
	}
}

const val LINK_SCORE_MINIMUM = 2