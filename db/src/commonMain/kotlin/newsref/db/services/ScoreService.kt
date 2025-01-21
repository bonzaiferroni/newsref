package newsref.db.services

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.DbService
import newsref.db.globalConsole
import newsref.db.tables.*
import newsref.db.tables.LinkTable
import newsref.db.tables.SourceTable
import newsref.db.utils.createOrUpdate
import newsref.db.utils.toInstantUtc
import newsref.db.utils.toLocalDateTimeUtc
import newsref.db.utils.toSqlString
import newsref.model.data.SourceScore
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlin.time.Duration

private val console = globalConsole.getHandle("ScoreService")

class ScoreService : DbService() {
	suspend fun readScores(sourceId: Long) = dbQuery {
		SourceScoreTable.select(SourceScoreTable.columns)
			.where { SourceScoreTable.sourceId eq sourceId }
			.orderBy(SourceScoreTable.scoredAt, SortOrder.ASC)
			.map { it.toSourceScore() }
	}

	suspend fun findScoreSignals(duration: Duration) = dbQuery {
		val time = (Clock.System.now() - duration).toLocalDateTimeUtc()

		LeadTable.leftJoin(LinkTable).leftJoin(LeadJobTable)
			.leftJoin(SourceTable, LinkTable.sourceId, SourceTable.id)
			.select(LeadTable.sourceId, LeadTable.id, LeadTable.url, SourceTable.id, SourceTable.hostId, SourceTable.score,
				SourceTable.publishedAt, SourceTable.seenAt, LeadJobTable.freshAt, LeadJobTable.feedPosition,
				LeadJobTable.feedId
			)
			.where {
				((SourceTable.publishedAt.isNull() and SourceTable.seenAt.greater(time)) or
						SourceTable.publishedAt.greater(time) or LeadJobTable.freshAt.greater(time)) and
						LinkTable.isExternal.eq(true) and LeadTable.sourceId.isNotNull()
			}
			.toSqlString { console.log(it) }
			.map {
				ScoreSignal(
					targetId = it[LeadTable.sourceId]!!.value,
					leadId = it[LeadTable.id].value,
					url = it[LeadTable.url],
					originHostId = it.getOrNull(SourceTable.hostId)?.value,
					originId = it.getOrNull(SourceTable.id)?.value,
					originScore = it.getOrNull(SourceTable.score),
					linkedAt = (it.getOrNull(SourceTable.publishedAt)
						?: it.getOrNull(SourceTable.seenAt) ?: it.getOrNull(LeadJobTable.freshAt))!!.toInstantUtc(),
					feedPosition = it.getOrNull(LeadJobTable.feedPosition),
					feedId = it.getOrNull(LeadJobTable.feedId)?.value,
					linkId = it.getOrNull(LinkTable.id)?.value,
				)
			}
	}

	suspend fun addScores(scores: List<CalculatedScore>) = dbQuery {
		val scoreRows = scores.mapNotNull { (sourceId, score, scores) ->
			val sourceRow = SourceRow.findById(sourceId)
				?: throw IllegalArgumentException("source not found: $sourceId")
			val currentScore = sourceRow.score
			if (currentScore != null && currentScore >= score) return@mapNotNull null
			sourceRow.score = score

			if (score < MINIMUM_SCORE_RECORD) return@mapNotNull null
			SourceScoreTable.deleteWhere { SourceScoreTable.sourceId eq sourceId }
			SourceScoreTable.batchInsert(scores) {
				this[SourceScoreTable.sourceId] = it.sourceId
				this[SourceScoreTable.originId] = it.originId
				this[SourceScoreTable.feedId] = it.feedId
				this[SourceScoreTable.score] = it.score
				this[SourceScoreTable.scoredAt] = it.scoredAt.toLocalDateTimeUtc()
			}

			val sourceCollection = SourceTable.getCollections { SourceTable.id.eq(sourceId) }.firstOrNull()
				?: throw IllegalArgumentException("Source not found: $sourceId")
			FeedSourceRow.createOrUpdate(FeedSourceTable.sourceId eq sourceId) {
				source = sourceRow
				this.score = score
				createdAt = sourceRow.publishedAt ?: sourceRow.seenAt
				json = sourceCollection
			}
		}
		console.log("FeedSourceService: added ${scoreRows.size} scores")
	}
}

internal fun <T : Comparable<T>> ColumnSet.leftJoin(
	table: IdTable<T>,
	idColumn: Expression<*>?,
	otherIdColumn: Expression<*>?,
) = this.join(table, JoinType.LEFT, idColumn, otherIdColumn)

data class ScoreSignal(
	val targetId: Long,
	val leadId: Long,
	val url: String,
	val originHostId: Int?,
	val linkedAt: Instant,
	val feedPosition: Int?,
	val feedId: Int?,
	val originScore: Int?,
	val originId: Long?,
	val linkId: Long?,
)
data class CalculatedScore(val sourceId: Long, val score: Int, val scores: List<SourceScore>)

const val MINIMUM_SCORE_RECORD = 3
