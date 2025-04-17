package newsref.db.services

import klutch.db.DbService
import newsref.db.core.cosineDistance
import newsref.db.model.Chapter
import newsref.db.tables.ChapterAspect
import newsref.db.tables.ChapterTable
import newsref.db.tables.toChapter
import klutch.utils.toLocalDateTimeUtc
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.update

class ChapterLinkerService: DbService() {

    suspend fun readParentIsNull() = dbQuery {
        ChapterTable.select(ChapterAspect.columns)
            .where { ChapterTable.parentId.eq(null) and ChapterTable.size.greaterEq(CHAPTER_MIN_ARTICLES) }
            .map { it.toChapter() }
    }

    suspend fun findNearestParent(chapter: Chapter) = dbQuery {
        val time = chapter.averageAt.toLocalDateTimeUtc()
        val vector = ChapterTable.select(ChapterTable.vector)
            .where { ChapterTable.id.eq(chapter.id) }
            .firstOrNull()?.let { it[ChapterTable.vector] } ?: return@dbQuery null
        val distance = ChapterTable.vector.cosineDistance(vector).alias("cosine_distance")
        ChapterTable.select(ChapterAspect.columns + distance)
            .where { ChapterTable.id.neq(chapter.id) and ChapterTable.happenedAt.less(time) }
            .orderBy(distance, SortOrder.ASC)
            .firstOrNull()?.let { StorySignal(it[distance], it.toChapter()) }
    }

    suspend fun setParent(chapterId: Long, parentId: Long) = dbQuery {
        ChapterTable.update({ChapterTable.id.eq(chapterId)}) {
            it[ChapterTable.parentId] = parentId
        }
    }

    suspend fun readChildren(parentId: Long) = dbQuery {
        ChapterTable.select(ChapterAspect.columns)
            .where { ChapterTable.parentId.eq(parentId) }
            .map { it.toChapter() }
    }
}