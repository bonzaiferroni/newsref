package newsref.db.services

import kotlinx.datetime.Clock
import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.tables.ArticleTable
import newsref.db.tables.LinkTable
import newsref.db.tables.SourceTable
import newsref.db.utils.toLocalDateTimeUTC
import newsref.model.data.Link
import newsref.model.data.SourceScore
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

class ScoreService : DbService() {
	suspend fun findNewLinksSince(duration: Duration) = dbQuery {
		val time = (Clock.System.now() - duration).toLocalDateTimeUTC()

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
//		for ((sourceId, score) in scores) {
//			val now = Clock.System.now()
//			val sourceRow =
//				SourceRow.findById(sourceId) ?: throw IllegalArgumentException("source not found: $sourceId")
//			val lastScore = LinkScoreRow.find { LinkScoreTable.sourceId eq sourceId }.maxByOrNull { LinkScoreTable.id }
//			if (lastScore != null && lastScore.score == score
//				&& lastScore.scoredAt.toInstant(TimeZone.UTC) - now < SAME_SCORE_TIME_THRESHOLD)
//				continue
//
//			val linkScore = LinkScore(score = score, scoredAt = now)
//			LinkScoreRow.new { fromData(linkScore, sourceRow) }
	}
}


internal fun <T : Comparable<T>> ColumnSet.leftJoin(
	table: IdTable<T>,
	idColumn: Column<EntityID<T>>,
	otherIdColumn: Column<EntityID<T>>,
) = this.join(table, JoinType.LEFT, idColumn, otherIdColumn)

data class LinkItem(val link: Link, val sourceId: Long, val hostId: Int)

val SAME_SCORE_TIME_THRESHOLD = 1.hours
