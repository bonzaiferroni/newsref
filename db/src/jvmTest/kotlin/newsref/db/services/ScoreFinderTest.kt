package newsref.db.services

import newsref.db.DbTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.days

class ScoreFinderTest : DbTest(true) {

	@Test
	fun `explore code`(): Unit = dbQuery {
		val scoreFinderService = ScoreFinderService()
		val items = scoreFinderService.findNewLinksSince2(3.days)
		val targetSets = mutableMapOf<Long, MutableSet<String>>()
		for ((link, sourceCore) in items) {
			if (link.url.core.contains(".$sourceCore")) {
				continue
			}
			val targetId = link.targetId ?: throw Exception("Target must not be null: ${link.url}")
			(targetSets.getOrPut(targetId) { mutableSetOf() }).add(sourceCore)
		}
		val scores = targetSets.filter { it.value.size >= LINK_SCORE_MINIMUM }
			.map { Pair(it.key, it.value.size) }
		val top = scores.takeIf{ it.isNotEmpty()}?.sortedByDescending { it.second }?.take(5)
		println("looked at ${items.size} links, added ${scores.size} scores, top: ${top?.firstOrNull()?.second ?: 0}")
		top?.forEach { (id, score) -> items.firstOrNull { it.first.targetId == id }.let {
			println("${score}: ${it?.first?.url.toString()}")
		}}
	}
}

const val LINK_SCORE_MINIMUM = 5