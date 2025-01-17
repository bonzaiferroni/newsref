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
import newsref.model.data.Link
import newsref.model.data.SourceScore
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlin.time.Duration

private val console = globalConsole.getHandle("ScoreService")

class ScoreService : DbService() {
	suspend fun findNewLinksSince(duration: Duration) = dbQuery {
		val time = (Clock.System.now() - duration).toLocalDateTimeUtc()

		LeadTable.leftJoin(LinkTable).leftJoin(LeadJobTable)
			.leftJoin(SourceTable, LinkTable.sourceId, SourceTable.id)
			.select(
				LinkTable.columns + LeadTable.sourceId + LeadJobTable.feedPosition + SourceTable.hostId
						+ SourceTable.seenAt + SourceTable.publishedAt
			)
			.where {
				// SourceTable.seenAt.greaterEq(time) and
						LinkTable.isExternal.eq(true) and LeadTable.sourceId.isNotNull()
			}
			// .also { println(it.prepareSQL(QueryBuilder(false))) }
			.map {
				LinkTracer(
					link = it.toLink(),
					sourceId = it.getOrNull(LeadTable.sourceId)!!.value,
					hostId = it[SourceTable.hostId].value,
					linkedAt = (it.getOrNull(SourceTable.publishedAt) ?: it[SourceTable.seenAt]).toInstantUtc(),
					feedPosition = it.getOrNull(LeadJobTable.feedPosition),
				)
			}
	}

	suspend fun addScores(tracers: List<SourceTracer>) = dbQuery {
		val scoreRows = tracers.mapNotNull { (sourceId, score, scores) ->
			val sourceRow = SourceRow.findById(sourceId)
				?: throw IllegalArgumentException("source not found: $sourceId")
			val currentScore = sourceRow.score
			if (currentScore != null && currentScore >= score) return@mapNotNull null
			sourceRow.score = score

			if (score < MINIMUM_SCORE_RECORD) return@mapNotNull null
			SourceScoreTable.deleteWhere { SourceScoreTable.sourceId eq sourceId }
			SourceScoreTable.batchInsert(scores) {
				this[SourceScoreTable.sourceId] = it.sourceId
				this[SourceScoreTable.linkId] = it.linkId
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

data class LinkTracer(
	val link: Link,
	val sourceId: Long,
	val hostId: Int,
	val linkedAt: Instant,
	val feedPosition: Int?,
)
data class SourceTracer(val sourceId: Long, val score: Int, val scores: List<SourceScore>)

const val MINIMUM_SCORE_RECORD = 3
