package newsref.db.services

import kotlinx.datetime.Clock
import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.tables.ArticleTable
import newsref.db.tables.ContentTable
import newsref.db.tables.HostTable
import newsref.db.tables.LinkTable
import newsref.db.tables.SourceRow
import newsref.db.tables.SourceScoreRow
import newsref.db.tables.SourceScoreTable
import newsref.db.utils.toInstantUtc
import newsref.db.utils.toLocalDateTimeUtc
import newsref.model.dto.*
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import kotlin.time.Duration

class SourceInfoService : DbService() {
	suspend fun getSource(id: Long) = dbQuery {
		SourceTable.getInfos { SourceTable.id eq id }.firstOrNull()
	}

	suspend fun getTopSources(duration: Duration, limit: Int) = dbQuery {
		val time = (Clock.System.now() - duration).toLocalDateTimeUtc()
		FeedSourceTable.select(FeedSourceTable.json)
			.where {FeedSourceTable.createdAt greaterEq time}
			.orderBy(FeedSourceTable.score, SortOrder.DESC)
			.limit(limit)
			.map { it[FeedSourceTable.json] }
	}
}

internal fun SourceTable.getInfos(block: SqlExpressionBuilder.() -> Op<Boolean>): List<SourceInfo> {
	val sourceInfos = this.leftJoin(ArticleTable).leftJoin(HostTable).leftJoin(NoteTable)
		.select(
			SourceTable.id,
			this.url,
			this.title,
			this.score,
			this.imageUrl,
			this.thumbnail,
			this.seenAt,
			this.publishedAt,
			HostTable.core,
			HostTable.name,
			HostTable.logo,
			ArticleTable.headline,
			ArticleTable.description,
			ArticleTable.wordCount,
			ArticleTable.section,
			NoteTable.body
		)
		.where(block)
		.map { row ->
			SourceInfo(
				sourceId = row[SourceTable.id].value,
				url = row[this.url],
				pageTitle = row[this.title],
				score = row[this.score] ?: 0,
				image = row.getOrNull(this.imageUrl),
				thumbnail = row.getOrNull(this.thumbnail),
				seenAt = row[this.seenAt].toInstantUtc(),
				publishedAt = row.getOrNull(this.publishedAt)?.toInstantUtc(),
				hostCore = row[HostTable.core],
				hostName = row.getOrNull(HostTable.name),
				hostLogo = row.getOrNull(HostTable.logo),
				headline = row.getOrNull(ArticleTable.headline),
				description = row.getOrNull(ArticleTable.description),
				wordCount = row.getOrNull(ArticleTable.wordCount),
				section = row.getOrNull(ArticleTable.section),
				note = row.getOrNull(NoteTable.body),
				inLinks = emptyList(),
				outLinks = emptyList(),
				scores = emptyList(),
				notes = emptyList(),
				authors = null,
			)
		}
	return sourceInfos.map { sourceInfo ->
		val scoreRows = SourceScoreRow.find { SourceScoreTable.sourceId.eq(sourceInfo.sourceId) }
			.orderBy(Pair(SourceScoreTable.scoredAt, SortOrder.ASC))
		val inLinks = LinkTable.getLinkInfos { LeadTable.sourceId.eq(sourceInfo.sourceId) }
		val outLinks = LinkTable.getLinkInfos { LinkTable.sourceId.eq(sourceInfo.sourceId) }
		val authors = SourceRow.findById(sourceInfo.sourceId)?.authors?.map { it.name }
		val notes = SourceNoteTable.leftJoin(NoteTable).leftJoin(UserTable)
			.select(NoteTable.columns + UserTable.username)
			.where { SourceNoteTable.sourceId eq sourceInfo.sourceId}
			.map { NoteInfo(
				userId = it[NoteTable.userId].value,
				username = it[UserTable.username],
				subject = it[NoteTable.subject],
				body = it[NoteTable.body],
				createdAt = it[NoteTable.createdAt].toInstantUtc(),
				modifiedAt = it[NoteTable.modifiedAt].toInstantUtc(),
			)}

		sourceInfo.copy(
			inLinks = inLinks,
			outLinks = outLinks,
			scores = scoreRows.map { ScoreInfo(it.score, it.scoredAt.toInstantUtc()) },
			authors = authors,
			notes = notes
		)
	}
}

internal fun LinkTable.getLinkInfos(block: SqlExpressionBuilder.() -> Op<Boolean>): List<LinkInfo> {
	val linkInfos = this.leftJoin(SourceTable).leftJoin(HostTable)
		.leftJoin(LeadTable, this.leadId, LeadTable.id)
		.leftJoin(ArticleTable)
		.leftJoin(ContentTable)
		.select(
			url,
			urlText,
			sourceId,
			LeadTable.sourceId,
			ContentTable.text,
			SourceTable.url,
			SourceTable.seenAt,
			SourceTable.publishedAt,
			ArticleTable.headline,
			HostTable.name,
			HostTable.core,
		)
		.where(block)
//		.also { println(it.prepareSQL(QueryBuilder(false))) }
		.map { row ->
			LinkInfo(
				sourceId = row[sourceId].value,
				leadSourceId = row[LeadTable.sourceId]?.value,
				url = row[url],
				urlText = row[urlText],
				context = row.getOrNull(ContentTable.text),
				sourceUrl = row[SourceTable.url],
				hostName = row.getOrNull(HostTable.name),
				hostCore = row[HostTable.core],
				seenAt = row[SourceTable.seenAt].toInstantUtc(),
				publishedAt = row.getOrNull(SourceTable.publishedAt)?.toInstantUtc(),
				headline = row.getOrNull(ArticleTable.headline),
				authors = null
			)
		}
	return linkInfos.map { linkInfo ->
		val authors = AuthorTable.getAuthors { SourceAuthorTable.sourceId.eq(linkInfo.sourceId) }
			.takeIf { it.isNotEmpty() }
		val snippet = linkInfo.context?.findContainingSentence(linkInfo.urlText)
		linkInfo.copy(authors = authors, context = snippet)
	}
}

internal fun AuthorTable.getAuthors(block: SqlExpressionBuilder.() -> Op<Boolean>): List<PageAuthor> {
	return this.leftJoin(SourceAuthorTable).leftJoin(HostAuthorTable)
		.select(name, id, SourceAuthorTable.sourceId)
		.where(block)
		.mapNotNull { row -> PageAuthor(name = row[name], url = row.getOrNull(url)) }
}

fun String.findContainingSentence(substring: String): String? {
	val sentencePattern = """[^.!?]*[.!?]["”']?""".toRegex()
	return sentencePattern.findAll(this)
		.map { it.value.trim() }
		.firstOrNull { it.contains(substring, ignoreCase = true) }
		?.let { sentence ->
			if (sentence.first().isUpperCase() || openingChars.contains(sentence.first())) sentence else "...$sentence"
		}
}

private val openingChars = setOf('“', '\"')