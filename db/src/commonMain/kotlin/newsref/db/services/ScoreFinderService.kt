package newsref.db.services

import kotlinx.datetime.Clock
import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.tables.ArticleTable
import newsref.db.tables.HostTable
import newsref.db.tables.LinkTable
import newsref.db.tables.SourceTable
import newsref.db.utils.toLocalDateTimeUTC
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import kotlin.time.Duration

class ScoreFinderService : DbService() {
	suspend fun findNewLinksSince(duration: Duration) = dbQuery {
		val time = (Clock.System.now() - duration).toLocalDateTimeUTC()

		SourceTable.leftJoin(ArticleTable).leftJoin(HostTable)
			.join(LinkTable, JoinType.LEFT, SourceTable.id, LinkTable.sourceId)
			.select(LinkTable.columns + HostTable.core)
			.where {
				SourceTable.seenAt.greaterEq(time) and
						(ArticleTable.publishedAt.isNull() or ArticleTable.publishedAt.greaterEq(time)) and
						LinkTable.targetId.isNotNull() and LinkTable.isExternal.eq(true)
			}
			.map { Pair(it.toLink(), it[HostTable.core]) }
	}

	suspend fun findNewLinksSince2(duration: Duration) = dbQuery {
		val time = (Clock.System.now() - duration).toLocalDateTimeUTC()

		SourceTable.leftJoin(ArticleTable).leftJoin(HostTable)
			.join(LinkTable, JoinType.LEFT, SourceTable.id, LinkTable.sourceId)
			.select(LinkTable.columns + HostTable.core)
			.where {
				SourceTable.seenAt.greaterEq(time) and
						(ArticleTable.publishedAt.isNull() or ArticleTable.publishedAt.greaterEq(time)) and
						LinkTable.targetId.isNotNull() and LinkTable.isExternal.eq(true)
			}
			.map { Pair(it.toLink(), it[HostTable.core]) }
	}

	suspend fun setInternal(linkId: Long) = dbQuery {
		val linkRow = LinkRow.findById(linkId) ?: return@dbQuery false
		linkRow.isExternal = false
		true // return
	}

	suspend fun addScores(scores: List<Pair<Long, Int>>) = dbQuery {
		for ((sourceId, score) in scores) {
			val sourceRow = SourceRow.findById(sourceId) ?: throw IllegalArgumentException("source not found: $sourceId")
			val linkScore = LinkScore(score = score, scoredAt = Clock.System.now())
			LinkScoreRow.new { fromData(linkScore, sourceRow) }
		}
	}
}