package newsref.db.services

import klutch.db.DbService
import newsref.db.*
import newsref.db.tables.*
import org.jetbrains.exposed.sql.*

class StoryService: DbService() {
    suspend fun readAllStories() = dbQuery {
        StoryTable.selectAll()
            .map { it.toStory() }
    }

    suspend fun readStoryById(storyId: Long) = dbQuery {
        StoryTable.select(StoryTable.columns)
            .where { StoryTable.id eq storyId }
            .firstOrNull()?.toStory()
    }

    suspend fun readStoryChapters(storyId: Long) = dbQuery {
        ChapterTable.select(ChapterTable.columns)
            .where { ChapterTable.storyId eq storyId }
            .map { it.toChapter() }
    }
}