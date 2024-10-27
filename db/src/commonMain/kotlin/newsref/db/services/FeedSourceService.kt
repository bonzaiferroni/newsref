package newsref.db.services

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.tables.ArticleTable
import newsref.db.tables.HostTable
import newsref.db.tables.LinkTable
import newsref.db.tables.SourceScoreRow
import newsref.db.tables.SourceScoreTable
import newsref.db.tables.SourceTable
import newsref.db.utils.toInstantUtc
import newsref.model.data.FeedSource
import newsref.model.data.FeedSourceLink
import newsref.model.data.FeedSourceScore
import newsref.model.data.SourceScore
import org.jetbrains.exposed.sql.deleteAll

class FeedSourceService : DbService() {
	suspend fun getTopSources() = dbQuery {
		FeedSourceRow.all().map { it.toData() }
	}

	suspend fun addScores(scores: List<SourceScore>) = dbQuery {
		FeedSourceTable.deleteAll()
		scores.map { score ->
			val feedSource = SourceTable.leftJoin(ArticleTable).leftJoin(HostTable)
				.select(
					SourceTable.id,
					SourceTable.url,
					SourceTable.seenAt,
					HostTable.core,
					HostTable.name,
					HostTable.logo,
					ArticleTable.headline,
					ArticleTable.wordCount,
					ArticleTable.section,
					ArticleTable.thumbnail,
					ArticleTable.publishedAt,
				)
				.where { SourceTable.id.eq(score.sourceId) }
				.map { row ->
					FeedSource(
						sourceId = row[SourceTable.id].value,
						url = row[SourceTable.url],
						seenAt = row[SourceTable.seenAt].toInstant(TimeZone.UTC),
						hostCore = row[HostTable.core],
						hostName = row.getOrNull(HostTable.name),
						hostLogo = row.getOrNull(HostTable.logo),
						headline = row.getOrNull(ArticleTable.headline),
						wordCount = row.getOrNull(ArticleTable.wordCount),
						section = row.getOrNull(ArticleTable.section),
						thumbnail = row.getOrNull(ArticleTable.thumbnail),
						latestScore = score.score,
						publishedAt = row.getOrNull(ArticleTable.publishedAt)?.toInstant(TimeZone.UTC),
						inLinks = emptyList(),
						scores = emptyList(),
						authors = emptyList(),
					)
				}
				.first()
			val scoreRows = SourceScoreRow.find { SourceScoreTable.sourceId.eq(score.sourceId) }
			val feedSourceLinks = LinkTable.leftJoin(SourceTable).leftJoin(ArticleTable)
				.leftJoin(ContentTable).leftJoin(HostTable)
				.select(
					LinkTable.url,
					LinkTable.urlText,
					ContentTable.text,
					SourceTable.url,
					SourceTable.seenAt,
					ArticleTable.headline,
					ArticleTable.publishedAt,
					HostTable.name,
				)
				.where { LinkTable.id.eq(score.sourceId) }
				.map { row ->
					FeedSourceLink(
						url = row[LinkTable.url],
						urlText = row[LinkTable.urlText],
						context = row.getOrNull(ContentTable.text),
						sourceUrl = row[SourceTable.url],
						hostName = row.getOrNull(HostTable.name),
						seenAt = row[SourceTable.seenAt].toInstantUtc(),
						headline = row.getOrNull(ArticleTable.headline),
						publishedAt = row.getOrNull(ArticleTable.publishedAt)?.toInstantUtc(),
					)
				}
			val authors = SourceRow.findById(score.sourceId)?.authors?.mapNotNull { it.name }
			FeedSourceRow.new { fromData(feedSource.copy (
				inLinks = feedSourceLinks,
				scores = scoreRows.map { FeedSourceScore(it.score, it.scoredAt.toInstantUtc()) },
				authors = authors,
			)) }
		}
		Unit
	}
}