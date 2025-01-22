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
import newsref.db.utils.applyIfNotNull
import newsref.db.utils.toInstantUtc
import newsref.db.utils.toLocalDateTimeUtc
import newsref.model.dto.*
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.selectAll
import kotlin.time.Duration

class SourceService : DbService() {
    suspend fun getSourceCollection(id: Long) = dbQuery {
        SourceTable.getCollections { SourceTable.id eq id }.firstOrNull()
    }

    suspend fun getSourceInfo(id: Long) = dbQuery {
        sourceInfoTables.where { SourceTable.id eq id }
            .firstOrNull()?.toSourceInfo()
    }

    suspend fun getSource(id: Long) = dbQuery {
        SourceTable.select(SourceTable.columns)
            .where { SourceTable.id eq id }
            .firstOrNull()?.toSource()
    }

    suspend fun getSourceInfos(afterId: Long? = null, limit: Int = 100) = dbQuery {
        sourceInfoTables
            .applyIfNotNull(afterId) { this.where { SourceTable.id greater it } }
            .orderBy(SourceTable.id, SortOrder.DESC)
            .limit(limit)
            .map { it.toSourceInfo() }
    }

    suspend fun getTopSourceInfos(duration: Duration, limit: Int = 100) = dbQuery {
        val time = (Clock.System.now() - duration).toLocalDateTimeUtc()
        sourceInfoTables
            .where { SourceTable.seenAt.greater(time) }
            .orderBy(SourceTable.score, SortOrder.DESC_NULLS_LAST)
            .limit(limit)
            .map { it.toSourceInfo() }
    }

    suspend fun getSourceCount() = dbQuery {
        SourceTable.selectAll().count()
    }

    suspend fun getTopSources(duration: Duration, limit: Int) = dbQuery {
        val time = (Clock.System.now() - duration).toLocalDateTimeUtc()
        FeedSourceTable.select(FeedSourceTable.json)
            .where { FeedSourceTable.createdAt greaterEq time }
            .orderBy(FeedSourceTable.score, SortOrder.DESC)
            .limit(limit)
            .map { it[FeedSourceTable.json] }
    }
}

internal fun SourceTable.getCollections(block: SqlExpressionBuilder.() -> Op<Boolean>): List<SourceCollection> {
    val sourceInfos = sourceInfoTables
        .where(block)
        .map { it.toSourceInfo() }
    return sourceInfos.map { sourceInfo ->
        val scoreRows = SourceScoreRow.find { SourceScoreTable.sourceId.eq(sourceInfo.sourceId) }
            .orderBy(Pair(SourceScoreTable.scoredAt, SortOrder.ASC))
        val inLinks = LinkTable.readLinkCollections { LeadTable.sourceId.eq(sourceInfo.sourceId) }
        val outLinks = LinkTable.readLinkCollections { LinkTable.sourceId.eq(sourceInfo.sourceId) }
        val authors = SourceRow.findById(sourceInfo.sourceId)?.authors?.map { it.name }
        val notes = noteInfoJoins
            .where { SourceNoteTable.sourceId eq sourceInfo.sourceId }
            .map { it.toNoteInfo() }

        SourceCollection(
            info = sourceInfo,
            inLinks = inLinks,
            outLinks = outLinks,
            scores = scoreRows.map { ScoreInfo(it.score, it.scoredAt.toInstantUtc()) },
            authors = authors,
            notes = notes
        )
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