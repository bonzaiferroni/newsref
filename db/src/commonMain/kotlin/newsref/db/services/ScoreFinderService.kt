package newsref.db.services

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.tables.ArticleTable
import newsref.db.tables.HostTable
import newsref.db.tables.LinkTable
import newsref.db.tables.SourceTable
import newsref.db.utils.toLocalDateTimeUTC
import newsref.model.data.Link
import org.jetbrains.exposed.sql.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

class ScoreFinderService : DbService() {
	suspend fun findNewLinksSince(duration: Duration) = dbQuery {
		val time = (Clock.System.now() - duration).toLocalDateTimeUTC()

		LeadTable.leftJoin(LinkTable)
			.join(SourceTable, JoinType.LEFT, LinkTable.sourceId, SourceTable.id)
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

	suspend fun findNewLinksSince2(duration: Duration) = dbQuery {
		val time = (Clock.System.now() - duration).toLocalDateTimeUTC()

		SourceTable.leftJoin(ArticleTable).leftJoin(HostTable)
			.join(LinkTable, JoinType.LEFT, SourceTable.id, LinkTable.sourceId)
			.select(LinkTable.columns + HostTable.core)
			.where {
				SourceTable.seenAt.greaterEq(time) and
						(ArticleTable.publishedAt.isNull() or ArticleTable.publishedAt.greaterEq(time)) and
						LinkTable.leadId.isNotNull() and LinkTable.isExternal.eq(true)
			}
			.map { Pair(it.toLink(), it[HostTable.core]) }
	}

	suspend fun addScores(scores: List<ScoreGroup>) = dbQuery {
		for ((sourceId, score) in scores) {
			val now = Clock.System.now()
			val sourceRow =
				SourceRow.findById(sourceId) ?: throw IllegalArgumentException("source not found: $sourceId")
			val lastScore = LinkScoreRow.find { LinkScoreTable.sourceId eq sourceId }.maxByOrNull { LinkScoreTable.id }
			if (lastScore != null && lastScore.score == score
				&& lastScore.scoredAt.toInstant(TimeZone.UTC) - now < SAME_SCORE_TIME_THRESHOLD)
				continue

			val linkScore = LinkScore(score = score, scoredAt = now)
			LinkScoreRow.new { fromData(linkScore, sourceRow) }
		}
	}
}

data class LinkItem(val link: Link, val sourceId: Long, val hostId: Int)
data class ScoreGroup(val sourceId: Long, val count: Int)

val SAME_SCORE_TIME_THRESHOLD = 1.hours