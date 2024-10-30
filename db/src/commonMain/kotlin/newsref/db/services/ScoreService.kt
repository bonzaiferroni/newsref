package newsref.db.services

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import newsref.db.DbService
import newsref.db.globalConsole
import newsref.db.tables.*
import newsref.db.tables.ArticleTable
import newsref.db.tables.LinkTable
import newsref.db.tables.SourceTable
import newsref.db.utils.toLocalDateTimeUtc
import newsref.model.data.FeedSource
import newsref.model.data.Link
import newsref.model.data.SourceScore
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

class ScoreService : DbService() {
	suspend fun findNewLinksSince(duration: Duration) = dbQuery {
		val time = (Clock.System.now() - duration).toLocalDateTimeUtc()

		LeadTable.leftJoin(LinkTable)
			.leftJoin(SourceTable, LinkTable.sourceId, SourceTable.id)
			.leftJoin(ArticleTable)
			.select(LinkTable.columns + LeadTable.sourceId + SourceTable.hostId)
			.where {
				SourceTable.seenAt.greaterEq(time) and
						(ArticleTable.publishedAt.isNull() or ArticleTable.publishedAt.greaterEq(time)) and
						LinkTable.isExternal.eq(true) and LeadTable.sourceId.isNotNull()
			}
			// .also { println(it.prepareSQL(QueryBuilder(false))) }
			.map {
				LinkItem(
					link = it.toLink(),
					sourceId = it.getOrNull(LeadTable.sourceId)!!.value,
					hostId = it[SourceTable.hostId].value,
				)
			}
	}

	suspend fun addScores(scores: List<SourceScore>) = dbQuery {
		val now = Clock.System.now()
		val scoreRows = scores.mapNotNull { (sourceId, score) ->
			val sourceRow =
				SourceRow.findById(sourceId) ?: throw IllegalArgumentException("source not found: $sourceId")
			sourceRow.score = score

			if (score < MINIMUM_SCORE_RECORD) return@mapNotNull null

			val lastScore =
				SourceScoreRow.find { SourceScoreTable.sourceId eq sourceId }.maxByOrNull { SourceScoreTable.id }
			if (lastScore != null && lastScore.score == score
				&& lastScore.scoredAt.toInstant(TimeZone.UTC) - now < SAME_SCORE_TIME_THRESHOLD
			) return@mapNotNull null

			val linkScore = SourceScore(sourceId = sourceId, score = score, scoredAt = now)
			SourceScoreRow.new { fromData(linkScore, sourceRow) }
		}

		FeedSourceTable.deleteAll()
		val size = scores.filter { it.score >= MINIMUM_SCORE_RECORD }.map { score ->
			val sourceInfo = SourceTable.getInfos { SourceTable.id.eq(score.sourceId) }.firstOrNull()
				?: throw IllegalArgumentException("Source not found: ${score.sourceId}")
			val feedSource = FeedSource(source = sourceInfo)
			FeedSourceRow.new { fromData(feedSource) }
		}.size
		globalConsole.log("FeedSourceService: added $size scores")
	}
}


internal fun <T : Comparable<T>> ColumnSet.leftJoin(
	table: IdTable<T>,
	idColumn: Expression<*>?,
	otherIdColumn: Expression<*>?,
) = this.join(table, JoinType.LEFT, idColumn, otherIdColumn)

data class LinkItem(val link: Link, val sourceId: Long, val hostId: Int)

val SAME_SCORE_TIME_THRESHOLD = 1.hours
const val MINIMUM_SCORE_RECORD = 5