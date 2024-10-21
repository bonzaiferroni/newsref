package newsref.db.tables

import kotlinx.datetime.Instant

data class LinkScore(
	val id: Long = 0,
	val sourceId: Long = 0,
	val score: Int,
	val scoredAt: Instant
) {
}