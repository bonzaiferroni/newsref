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
import newsref.model.data.Article
import newsref.model.dto.LinkInfo
import newsref.model.dto.SourceInfo
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import kotlin.time.Duration

class SourceInfoService : DbService() {
	suspend fun getTopSources(quantity: Int, duration: Duration) = dbQuery {
		val time = (Clock.System.now() - duration).toLocalDateTimeUTC()
		val sinceDuration = SourceTable.seenAt greaterEq time
		val isExternal = LinkTable.isExternal eq true
		val topSources = SourceTable.join(LinkTable, JoinType.LEFT, SourceTable.id, LinkTable.targetId)
			.select(SourceTable.id, LinkTable.targetId.count())
			.where(sinceDuration and isExternal)
			.orderBy(LinkTable.targetId.count(), SortOrder.DESC)
			.groupBy(SourceTable.id)
			.limit(quantity)
			.associate { it[SourceTable.id].value to it[LinkTable.targetId.count()] }
		val topIds = topSources.keys

		SourceTable.getSourceInfosByIds(topIds, topSources)
	}

	suspend fun getSourceById(id: Long) = dbQuery {
		val inLinks = LinkTable.getInLinksByTarget(id)
		val outLinks = LinkTable.getOutLinksBySource(id)
		SourceTable.getSourceInfoById(id, inLinks, outLinks)
	}
}

internal fun SourceTable.getSourceInfoById(
	id: Long,
	inLinks: List<LinkInfo>? = null,
	outLinks: List<LinkInfo>? = null,
) =
	this.leftJoin(ArticleTable).leftJoin(HostTable)
		.select(sourceInfoColumns)
		.where { SourceTable.id eq id }
		.wrapSourceInfo(null, inLinks, outLinks)
		.firstOrNull()

internal fun SourceTable.getSourceInfosByIds(ids: Set<Long>, citationCounts: Map<Long, Long>? = null) =
	this.leftJoin(ArticleTable).leftJoin(HostTable)
		.select(sourceInfoColumns)
		.where { SourceTable.id inList ids }
		.wrapSourceInfo(citationCounts)

internal val sourceInfoColumns = listOf(
	SourceTable.id,
	SourceTable.url,
	SourceTable.leadTitle,
	SourceTable.type,
	SourceTable.seenAt,
	HostTable.core,
	HostTable.name,
	HostTable.logo,
) + ArticleTable.columns

internal fun Query.wrapSourceInfo(
	citationCounts: Map<Long, Long>? = null,
	inLinks: List<LinkInfo>? = null,
	outLinks: List<LinkInfo>? = null,
) = this.map { row ->
	SourceInfo(
		id = row[SourceTable.id].value,
		url = row[SourceTable.url],
		leadTitle = row.getOrNull(SourceTable.leadTitle),
		type = row.getOrNull(SourceTable.type),
		seenAt = row[SourceTable.seenAt].toInstant(TimeZone.UTC),
		hostCore = row[HostTable.core],
		hostName = row.getOrNull(HostTable.name),
		hostLogo = row.getOrNull(HostTable.logo),
		article = row.getOrNull(ArticleTable.id)?.let {
			Article(
				id = it.value,
				headline = row[ArticleTable.headline],
				alternativeHeadline = row.getOrNull(ArticleTable.alternativeHeadline),
				description = row.getOrNull(ArticleTable.description),
				cannonUrl = row.getOrNull(ArticleTable.cannonUrl),
				imageUrl = row.getOrNull(ArticleTable.imageUrl),
				section = row.getOrNull(ArticleTable.section),
				keywords = row.getOrNull(ArticleTable.keywords),
				wordCount = row.getOrNull(ArticleTable.wordCount),
				isFree = row.getOrNull(ArticleTable.isFree),
				thumbnail = row.getOrNull(ArticleTable.thumbnail),
				language = row.getOrNull(ArticleTable.language),
				commentCount = row.getOrNull(ArticleTable.commentCount),
				accessedAt = row[ArticleTable.accessedAt].toInstant(TimeZone.UTC),
				publishedAt = row.getOrNull(ArticleTable.publishedAt)?.toInstant(TimeZone.UTC),
				modifiedAt = row.getOrNull(ArticleTable.modifiedAt)?.toInstant(TimeZone.UTC)
			)
		},
		citationCount = citationCounts?.get(row[SourceTable.id].value)?.toInt(),
		inLinks = inLinks,
		outLinks = outLinks,
	)
}

internal fun LinkTable.getInLinksByTarget(targetId: Long) =
	this.join(SourceTable, JoinType.LEFT, SourceTable.id, this.sourceId).leftJoin(ContentTable)
		.select(linkInfoColumns)
		.where { LinkTable.targetId eq targetId }
		.wrapLinkInfo()

internal fun LinkTable.getOutLinksBySource(sourceId: Long) =
	this.join(SourceTable, JoinType.LEFT, SourceTable.id, this.targetId).leftJoin(ContentTable)
		.select(linkInfoColumns)
		.where { LinkTable.sourceId eq sourceId }
		// .also { println(it.prepareSQL(QueryBuilder(false))) }
		.wrapLinkInfo()

internal val linkInfoColumns = listOf(
	LinkTable.targetId,
	LinkTable.sourceId,
	LinkTable.url,
	LinkTable.urlText,
	LinkTable.isExternal,
	ContentTable.text,
	SourceTable.url,
	SourceTable.seenAt,
)

internal fun Query.wrapLinkInfo() = this.map { row ->
	LinkInfo(
		targetId = row[LinkTable.targetId]?.value,
		sourceId = row[LinkTable.sourceId].value,
		url = row[LinkTable.url],
		urlText = row[LinkTable.urlText],
		context = row.getOrNull(ContentTable.text),
		sourceUrl = row[SourceTable.url],
		seenAt = row[SourceTable.seenAt].toInstant(TimeZone.UTC)
	)
}