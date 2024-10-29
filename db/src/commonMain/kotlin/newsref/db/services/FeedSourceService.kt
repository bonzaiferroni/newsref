package newsref.db.services

import newsref.db.DbService
import newsref.db.globalConsole
import newsref.db.tables.*
import newsref.db.tables.SourceTable
import newsref.model.data.FeedSource
import newsref.model.data.SourceScore
import org.jetbrains.exposed.sql.deleteAll

class FeedSourceService : DbService() {
	suspend fun getTopSources() = dbQuery {
		FeedSourceRow.all().map { it.toData() }
	}

	suspend fun addScores(scores: List<SourceScore>) = dbQuery {
		FeedSourceTable.deleteAll()
		val size = scores.filter { it.score >= MINIMUM_SCORE_RECORD }.map { score ->
			val sourceInfo = SourceTable.getInfos { SourceTable.id.eq(score.sourceId) }.firstOrNull()
				?: throw IllegalArgumentException("Source not found: ${score.sourceId}")
			val feedSource = FeedSource(source = sourceInfo)
			FeedSourceRow.new { fromData(feedSource) }
		}.size
		globalConsole.log("FeedSourceService: added $size scores")
		Unit
	}
}