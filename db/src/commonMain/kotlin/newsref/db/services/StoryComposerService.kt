package newsref.db.services

import newsref.db.DbService
import newsref.db.core.cosineDistance
import newsref.db.globalConsole
import newsref.db.tables.*
import newsref.db.utils.toLocalDateTimeUtc
import newsref.db.model.Chapter
import newsref.db.model.Story
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

private val console = globalConsole.getHandle("StoryComposerService")

class StoryComposerService : DbService() {

    suspend fun readStoryById(storyId: Long) = dbQuery {
        StoryTable.select(storyColumns)
            .where { StoryTable.id.eq(storyId) }
            .firstOrNull()?.toStory()
    }

    suspend fun readNullStoryDistances() = dbQuery {
        ChapterTable.select(ChapterTable.columns)
            .where {
                (ChapterTable.storyDistance.isNull() or ChapterTable.storyId.isNull()) and
                        ChapterTable.size.greaterEq(CHAPTER_MIN_ARTICLES)
            }
            .map { Pair(it.toChapter(), it[ChapterTable.vector]) }
    }

    suspend fun readStoryChapterVectors(storyId: Long) = dbQuery {
        ChapterTable.select(ChapterTable.columns)
            .where { ChapterTable.storyId.eq(storyId) and ChapterTable.storyDistance.isNotNull() }
            .map { Pair(it.toChapter(), it[ChapterTable.vector]) }
    }

    suspend fun readNearestStoryDistance(vector: FloatArray, storyId: Long?) = dbQuery {
        val distance = StoryTable.vector.cosineDistance(vector).alias("cosine_distance")
        StoryTable.select(storyColumns + distance)
            .where { StoryTable.id.neq(storyId ?: 0) }
            .orderBy(distance, SortOrder.ASC)
            .firstOrNull()?.let { Pair(it.toStory(), it[distance]) }
    }

    suspend fun updateStory(
        story: Story,
        averageVector: FloatArray,
        chapterDistances: List<Pair<Chapter, Float>>
    ) = dbQuery {
        val ids = chapterDistances.map { it.first.id }
        StoryTable.update({ StoryTable.id.eq(story.id) }) {
            it[StoryTable.vector] = averageVector
            it[StoryTable.size] = chapterDistances.size
            it[StoryTable.title] = chapterDistances.maxBy { it.first.score }.first.title ?: story.title
            it[StoryTable.score] = chapterDistances.sumOf { it.first.score }
            it[StoryTable.happenedAt] = chapterDistances.map { it.first.happenedAt }.averageInstant()
                .toLocalDateTimeUtc()
            it[StoryTable.coherence] = (chapterDistances.sumOf { it.second.toDouble() } / chapterDistances.size)
                .toFloat()
        }
        ChapterTable.update({ ChapterTable.storyId.eq(story.id) and ChapterTable.id.notInList(ids) }) {
            it[ChapterTable.storyId] = null
            it[ChapterTable.storyDistance] = null
        }
        for ((chapter, distance) in chapterDistances) {
            ChapterTable.update({ ChapterTable.id.eq(chapter.id) }) {
                it[ChapterTable.storyId] = story.id
                it[ChapterTable.storyDistance] = distance
            }
        }
    }

    suspend fun createStory(chapter: Chapter, vector: FloatArray) = dbQuery {
        val id = StoryTable.insertAndGetId {
            it[StoryTable.size] = 1
            it[StoryTable.happenedAt] = chapter.happenedAt.toLocalDateTimeUtc()
            it[StoryTable.score] = chapter.score
            it[StoryTable.title] = chapter.title
            it[StoryTable.coherence] = 0f
            it[StoryTable.vector] = vector
        }.value
        ChapterTable.update({ ChapterTable.id.eq(chapter.id) }) {
            it[ChapterTable.storyId] = id
            it[ChapterTable.storyDistance] = 0f
        }
    }

    suspend fun readChapterVector(chapterId: Long) = dbQuery {
        ChapterTable.select(ChapterTable.vector)
            .where { ChapterTable.id.eq(chapterId) }
            .firstOrNull()?.let { it[ChapterTable.vector] }
    }

    suspend fun readAllBelowSizeOrNull() = dbQuery {
        StoryTable.select(storyColumns)
            .where { StoryTable.size.isNull() or StoryTable.size.less(2) }
            .map { it.toStory() }
    }

    suspend fun readStoryChapters(storyId: Long) = dbQuery {
        ChapterTable.select(ChapterAspect.columns)
            .where { ChapterTable.storyId.eq(storyId) }
            .map { it.toChapter() }
    }

    suspend fun updateSize(storyId: Long, size: Int, score: Int) = dbQuery {
        StoryTable.update({ StoryTable.id.eq(storyId) }) {
            it[StoryTable.size] = size
            it[StoryTable.score] = score
        }
    }

    suspend fun updateSize(storyId: Long) = dbQuery {
        val children = readStoryChapters(storyId)
        val size = children.size
        if (size < 2) {
            deleteStory(storyId)
        } else {
            val score = children.sumOf { it.score }
            updateSize(storyId, size, score)
        }
    }

    suspend fun deleteStory(storyId: Long) = dbQuery {
        StoryTable.deleteWhere { StoryTable.id.eq(storyId) }
    }
}

data class StorySignal(
    val distance: Float,
    val chapter: Chapter,
)

const val STORY_MAX_DISTANCE = .25f
const val MIN_STORY_SIZE = 10