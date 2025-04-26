package newsref.db.services

import klutch.db.DbService
import klutch.db.cosineDistance
import klutch.db.read
import klutch.utils.since
import klutch.utils.toLocalDateTimeUtc
import kotlinx.datetime.Instant
import newsref.db.*
import newsref.db.model.*
import newsref.db.tables.*
import newsref.model.data.ContentType
import newsref.model.data.SourceType
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.notInList
import kotlin.time.Duration.Companion.days

private val console = globalConsole.getHandle("ChapterComposerService")

class ChapterComposerService : DbService() {
    suspend fun findNextSignal(excludedIds: List<Long>) = dbQuery {
        val subquery = ChapterPageTable.select(ChapterPageTable.pageId)
        val isNotExcluded = Op.build { PageTable.id.notInList(excludedIds) and PageTable.id.notInSubQuery(subquery) }
        val isValidArticle = Op.build {
            PageTable.contentType.eq(ContentType.NewsArticle) and PageTable.cachedWordCount.greaterEq(100) and
                    (PageTable.score.greater(1) or PageTable.feedPosition.lessEq(5))
        }
        val isValidReference = Op.build {
            PageTable.contentType.neq(ContentType.NewsArticle) and PageTable.score.greaterEq(3)
        }
        PageTable.read { isNotExcluded and (isValidArticle or isValidReference) }
            .orderBy(PageTable.seenAt, SortOrder.DESC)
            .firstOrNull()?.toChapterSignal()
    }

    suspend fun findTextRelatedChapters(chapterId: Long, pageIds: List<Long>, vector: FloatArray) = dbQuery {
        val distance = ChapterTable.vector.cosineDistance(vector).alias("cosine_distance")
        val excluded = ChapterExclusionTable.select(ChapterExclusionTable.chapterId).where {
            ChapterExclusionTable.chapterId.eq(chapterId) and ChapterExclusionTable.pageId.inList(pageIds)
        }
        ChapterTable.select(ChapterAspect.columns + distance)
            .where {
                ChapterTable.happenedAt.since(CHAPTER_EPOCH * 4) and
                        ChapterTable.id.notInSubQuery(excluded) and
                        ChapterTable.id.neq(chapterId)
            }
            .map { it.toChapter() to it[distance] }
            .filter { it.second < CHAPTER_MAX_DISTANCE }
    }

    suspend fun readChapterSourceSignals(chapterId: Long) = dbQuery {
        ChapterPageTable.leftJoin(PageTable)
            .select(PageTable.columns + ChapterPageTable.columns)
            .where {
                ChapterPageTable.chapterId.eq(chapterId) and
                        ChapterPageTable.sourceType.eq(SourceType.Article)
            }
            .map { it.toChapterSignal() }
    }

    suspend fun updateChapterAndSources(
        chapterId: Long,
        score: Int,
        size: Int,
        cohesion: Float,
        happenedAt: Instant,
        sources: List<ChapterPage>,
        vector: FloatArray,
    ) = dbQuery {
        ChapterTable.update({ ChapterTable.id.eq(chapterId) }) {
            it[ChapterTable.score] = score
            it[ChapterTable.size] = size
            it[ChapterTable.cohesion] = cohesion
            it[ChapterTable.happenedAt] = happenedAt.toLocalDateTimeUtc()
            it[ChapterTable.vector] = vector
            it[ChapterTable.parentId] = null
        }
        ChapterTable.update({ ChapterTable.parentId.eq(chapterId) }) {
            it[ChapterTable.parentId] = null
        }
        updateAndTrimSources(chapterId, sources)
    }

    fun Transaction.updateAndTrimSources(chapterId: Long, sources: List<ChapterPage>) {
        val pageIds = sources.map { it.pageId }
        ChapterPageTable.deleteWhere {
            ChapterPageTable.chapterId.eq(chapterId) and pageId.notInList(pageIds)
        }
        for (source in sources) {
            ChapterPageTable.upsert(ChapterPageTable.chapterId, ChapterPageTable.pageId) {
                it[ChapterPageTable.chapterId] = chapterId
                it[pageId] = source.pageId
                it[sourceType] = source.type
                it[distance] = source.distance
                it[linkDistance] = source.linkDistance
                it[timeDistance] = source.timeDistance
                it[textDistance] = source.textDistance
            }
        }
    }

    suspend fun deleteChapter(chapterId: Long) = dbQuery {
        ChapterTable.deleteWhere { ChapterTable.id.eq(chapterId) }
    }

    suspend fun createChapter(chapter: Chapter, sources: List<ChapterPage>, vector: FloatArray) = dbQuery {
        val chapterId = ChapterTable.insertAndGetId {
            it.fromModel(chapter)
            it[ChapterTable.vector] = vector
        }.value
        updateAndTrimSources(chapterId, sources)
        chapterId
    }

    suspend fun readInboundSignals(pageId: Long) = dbQuery {
        LinkTable.leftJoin(LeadTable).join(PageTable, JoinType.LEFT, LinkTable.pageId, PageTable.id)
            .select(PageTable.columns)
            .where { LeadTable.pageId.eq(pageId) and PageTable.cachedWordCount.greaterEq(EMBEDDING_MIN_WORDS) }
            .map { it.toChapterSignal() }
    }

    suspend fun readChapterSources(pageId: Long) = dbQuery {
        ChapterPageTable.select(ChapterPageTable.columns)
            .where { ChapterPageTable.pageId.eq(pageId) }
            .map { it.toChapterSource() }
    }
}

internal fun ResultRow.toChapterSignal() = this.let {
    val source = this.toPage()
    val chapterSource = when {
        this.getOrNull(ChapterPageTable.id) != null -> this.toChapterSource()
        else -> null
    }
    val outboundIds = LinkTable.leftJoin(LeadTable).select(LeadTable.pageId)
        .where {
            LinkTable.pageId.eq(source.id) and
                    LinkTable.isExternal.eq(true) and
                    LeadTable.pageId.isNotNull()
        }
        .groupBy(LeadTable.pageId)
        .map { it[LeadTable.pageId]!!.value }
        .toSet()
    ChapterPageSignal(source, chapterSource, outboundIds)
}

const val CHAPTER_MIN_ARTICLES = 3
const val ORIGIN_MIN_SCORE = MAX_LINK_SIGNAL * CHAPTER_MIN_ARTICLES
const val CHAPTER_MAX_DISTANCE = .2f
const val CHAPTER_MERGE_FACTOR = .75f
val CHAPTER_EPOCH = 5.days

data class ChapterPageSignal(
    val page: Page,
    val chapterPage: ChapterPage?,
    val linkIds: Set<Long>
)

fun List<Instant>.averageInstant() = Instant.fromEpochSeconds(this.sumOf { it.epochSeconds } / this.size)