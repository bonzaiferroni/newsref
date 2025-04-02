package newsref.db.tables

import newsref.db.core.vector
import newsref.db.tables.StoryTable.coherence
import newsref.db.tables.StoryTable.happenedAt
import newsref.db.tables.StoryTable.score
import newsref.db.tables.StoryTable.size
import newsref.db.tables.StoryTable.title
import newsref.db.utils.*
import newsref.db.model.Story
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object StoryTable : LongIdTable("story") {
    val title = text("title").nullable()
    val size = integer("size")
    val score = integer("score")
    val coherence = float("coherence")
    val happenedAt = datetime("happened_at")
    val vector = vector("vector", 768)
}

internal fun ResultRow.toStory() = Story(
    id = this[StoryTable.id].value,
    title = this[title],
    size = this[size],
    score = this[score],
    coherence = this[coherence],
    happenedAt = this[happenedAt].toInstantUtc()
)

internal val storyColumns = listOf(
    StoryTable.id,
    StoryTable.title,
    StoryTable.size,
    StoryTable.score,
    StoryTable.coherence,
    StoryTable.happenedAt,
)