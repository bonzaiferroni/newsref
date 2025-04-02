package newsref.db.services

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.tables.LinkTable
import newsref.db.tables.PageScoreTable
import newsref.db.utils.applyIfNotNull
import newsref.db.utils.sameUrl
import newsref.db.utils.toLocalDateTimeUtc
import newsref.db.core.Url
import newsref.db.utils.read
import newsref.model.dto.*
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import kotlin.time.Duration

class PageService : DbService() {
    suspend fun getSourceCollection(id: Long) = dbQuery {
        PageTable.getCollections { PageTable.id eq id }.firstOrNull()
    }

    suspend fun getSourceInfo(id: Long) = dbQuery {
        sourceInfoTables.where { PageTable.id eq id }
            .firstOrNull()?.toPageInfo()
    }

    suspend fun readPageById(id: Long) = dbQuery {
        PageTable.select(PageTable.columns)
            .where { PageTable.id eq id }
            .firstOrNull()?.toPage()
    }

    suspend fun getSourceInfos(searchText: String? = null, limit: Int = 100) = dbQuery {
        sourceInfoTables
            .applyIfNotNull(searchText) { this.where { PageTable.title.like("$it%") or PageTable.url.like("$it%") } }
            .orderBy(PageTable.id, SortOrder.DESC)
            .limit(limit)
            .map { it.toPageInfo() }
    }

    suspend fun getTopSourceInfos(duration: Duration, limit: Int = 100) = dbQuery {
        val time = (Clock.System.now() - duration).toLocalDateTimeUtc()
        sourceInfoTables
            .where { PageTable.seenAt.greater(time) }
            .orderBy(PageTable.score, SortOrder.DESC_NULLS_LAST)
            .limit(limit)
            .map { it.toPageInfo() }
    }

    suspend fun getPageCount() = dbQuery {
        PageTable.selectAll().count()
    }

    suspend fun getTopSources(duration: Duration, limit: Int) = dbQuery {
        val time = (Clock.System.now() - duration).toLocalDateTimeUtc()
        PageCacheTable.select(PageCacheTable.json)
            .where { PageCacheTable.createdAt greaterEq time }
            .orderBy(PageCacheTable.score, SortOrder.DESC)
            .limit(limit)
            .map { it[PageCacheTable.json] }
    }

    suspend fun readSourceByUrl(url: Url) = dbQuery {
        PageTable.selectAll()
            .where { PageTable.url.sameUrl(url) }
            .firstOrNull()?.toPage()
    }
}

internal fun PageTable.getCollections(block: SqlExpressionBuilder.() -> Op<Boolean>): List<PageCollection> {
    val sourceInfos = sourceInfoTables
        .where(block)
        .map { it.toPageInfo() }
    return sourceInfos.map { sourceInfo ->
        val scores = PageScoreTable.read { it.pageId.eq(sourceInfo.pageId) }
            .orderBy(Pair(PageScoreTable.scoredAt, SortOrder.ASC))
            .map { it.toSourceScore() }
        val inLinks = LinkTable.readLinkCollections { LeadTable.pageId.eq(sourceInfo.pageId) }
        val outLinks = LinkTable.readLinkCollections { LinkTable.pageId.eq(sourceInfo.pageId) }
        val authors = PageAuthorTable.leftJoin(AuthorTable).select(AuthorTable.name)
            .where { PageAuthorTable.pageId.eq(sourceInfo.pageId) }
            .map { it[AuthorTable.name] }
        val notes = noteInfoJoins
            .where { PageNoteTable.pageId eq sourceInfo.pageId }
            .map { it.toNoteInfo() }

        PageCollection(
            info = sourceInfo,
            inLinks = inLinks,
            outLinks = outLinks,
            scores = scores.map { ScoreInfo(it.score, it.scoredAt) },
            authors = authors,
            notes = notes
        )
    }
}


internal fun AuthorTable.getAuthors(block: SqlExpressionBuilder.() -> Op<Boolean>): List<CrawledAuthor> {
    return this.leftJoin(PageAuthorTable).leftJoin(HostAuthorTable)
        .select(name, id, PageAuthorTable.pageId)
        .where(block)
        .mapNotNull { row -> CrawledAuthor(name = row[name], url = row.getOrNull(url)) }
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

internal fun PageTable.withinTimeRange(start: Instant, end: Instant): Op<Boolean> {
    val timeStart = start.toLocalDateTimeUtc()
    val timeEnd = end.toLocalDateTimeUtc()
    return Op.build {
        (publishedAt.greaterEq(timeStart) and publishedAt.less(timeEnd)) or
                (publishedAt.isNull() and seenAt.greater(timeStart) and seenAt.less(timeEnd))
    }
}