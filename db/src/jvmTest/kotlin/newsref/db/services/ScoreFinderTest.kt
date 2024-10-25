package newsref.db.services

import newsref.db.DbTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.days

class ScoreFinderTest : DbTest(true) {

	@Test
	fun `explore code`(): Unit = dbQuery {
		val scoreFinderService = ScoreFinderService()
		val items = scoreFinderService.findNewLinksSince(3.days)
		val sourceIds = items.map { it.sourceId }.toSet()
		val scores = sourceIds.map { sourceId ->
			val score = items.filter { it.sourceId == sourceId }.map { it.hostId }.toSet().size
			ScoreGroup(sourceId, score)
		}
		val top = scores.takeIf{ it.isNotEmpty()}?.sortedByDescending { it.count }?.take(5)
		println("looked at ${items.size} links, added ${scores.size} scores, top: ${top?.firstOrNull()?.count ?: 0}")
		top?.forEach { (id, score) -> items.firstOrNull { it.sourceId == id }.let {
			println("${score}: ${it?.link?.url.toString()}")
		}}
	}
}

const val LINK_SCORE_MINIMUM = 2