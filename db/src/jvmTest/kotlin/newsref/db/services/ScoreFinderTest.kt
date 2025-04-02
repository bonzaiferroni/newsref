package newsref.db.services

import newsref.db.DbTest
import kotlin.test.Test

class ScoreFinderTest : DbTest(true) {

	@Test
	fun `explore code`(): Unit = dbQuery {
//		val now = Clock.System.now()
//		val scoreService = ScoreService()
//		val items = scoreService.findNewLinksSince(3.days)
//		val pageIds = items.map { it.pageId }.toSet()
//		val scores = pageIds.map { pageId ->
//			val score = items.filter { it.pageId == pageId }.map { it.hostId }.toSet().size
//			SourceScore(pageId, score, now)
//		}
//		val top = scores.takeIf{ it.isNotEmpty()}?.sortedByDescending { it.score }?.take(5)
//		println("looked at ${items.size} links, added ${scores.size} scores, top: ${top?.firstOrNull()?.score ?: 0}")
//		top?.forEach { (id, score) -> items.firstOrNull { it.pageId == id }.let {
//			println("${score}: ${it?.link?.url.toString()}")
//		}}
	}
}

const val LINK_SCORE_MINIMUM = 2