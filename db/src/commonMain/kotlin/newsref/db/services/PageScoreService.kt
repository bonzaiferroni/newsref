package newsref.db.services

import klutch.db.DbService
import klutch.utils.toInstantUtc
import klutch.utils.toLocalDateTimeUtc
import kotlinx.datetime.*
import newsref.db.*
import newsref.db.model.PageScore
import newsref.db.tables.*
import newsref.db.utils.*
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlin.time.Duration

private val console = globalConsole.getHandle("PageScoreService")

class PageScoreService : DbService() {
	suspend fun readScores(pageId: Long) = dbQuery {
		PageScoreTable.select(PageScoreTable.columns)
			.where { PageScoreTable.pageId eq pageId }
			.orderBy(PageScoreTable.scoredAt, SortOrder.ASC)
			.map { it.toSourceScore() }
	}

	suspend fun findScoreSignals(duration: Duration) = dbQuery {
		val time = (Clock.System.now() - duration)

		LeadTable.leftJoin(LinkTable).leftJoin(LeadJobTable)
			.leftJoin(PageTable, LinkTable.pageId, PageTable.id)
			.select(LeadTable.pageId, LeadTable.id, LeadTable.url, PageTable.id, PageTable.hostId, PageTable.score,
				PageTable.publishedAt, PageTable.seenAt, PageTable.feedPosition,
				LeadJobTable.freshAt, LeadJobTable.feedPosition, LeadJobTable.feedId
			)
			.where {
				PageTable.existedAfter(time) and LinkTable.isExternal.eq(true) and LeadTable.pageId.isNotNull()
			}
			// .toSqlString { console.log(it) }
			.map {
				ScoreSignal(
					targetId = it[LeadTable.pageId]!!.value,
					leadId = it[LeadTable.id].value,
					url = it[LeadTable.url],
					originHostId = it.getOrNull(PageTable.hostId)?.value,
					originId = it.getOrNull(PageTable.id)?.value,
					originScore = it.getOrNull(PageTable.score),
					linkedAt = (it.getOrNull(PageTable.publishedAt)
						?: it.getOrNull(PageTable.seenAt) ?: it.getOrNull(LeadJobTable.freshAt))!!.toInstantUtc(),
					feedPosition = it.getOrNull(PageTable.feedPosition) ?: it.getOrNull(LeadJobTable.feedPosition),
					feedId = it.getOrNull(LeadJobTable.feedId)?.value,
					linkId = it.getOrNull(LinkTable.id)?.value,
				)
			}
	}

	suspend fun addScores(scores: List<CalculatedScore>) = dbQuery {
		val updatedIds = scores.mapNotNull { (pageId, score, scores) ->
			val page = PageTable.readById(pageId).toPage()
			val currentScore = page.score
			if (currentScore != null && currentScore >= score) return@mapNotNull null
			PageTable.updateById(pageId) {
				it[this.score] = score
			}

			if (score < MINIMUM_SCORE_RECORD) return@mapNotNull null
			PageScoreTable.deleteWhere { PageScoreTable.pageId eq pageId }
			PageScoreTable.batchInsert(scores) {
				this[PageScoreTable.pageId] = it.pageId
				this[PageScoreTable.originId] = it.originId
				this[PageScoreTable.feedId] = it.feedId
				this[PageScoreTable.score] = it.score
				this[PageScoreTable.scoredAt] = it.scoredAt.toLocalDateTimeUtc()
			}

			pageId
		}
		console.log("FeedSourceService: added ${updatedIds.size} scores")
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
data class CalculatedScore(val pageId: Long, val score: Int, val scores: List<PageScore>)

const val MINIMUM_SCORE_RECORD = 3
const val MAX_LINK_SIGNAL = 3
const val MAX_FEED_SIGNAL = 3
