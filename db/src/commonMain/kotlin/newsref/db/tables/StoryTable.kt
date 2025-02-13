package newsref.db.tables

import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import kotlinx.serialization.json.Json
import newsref.db.core.vector
import newsref.db.tables.StoryTable.coherence
import newsref.db.tables.StoryTable.happenedAt
import newsref.db.tables.StoryTable.score
import newsref.db.tables.StoryTable.size
import newsref.db.tables.StoryTable.title
import newsref.db.utils.*
import newsref.model.core.*
import newsref.model.data.Story
import newsref.model.dto.*
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object StoryTable : LongIdTable("story") {
    val title = text("title").nullable()
    val size = integer("size")
    val score = integer("score")
    val coherence = float("coherence")
    val happenedAt = datetime("happened_at")
    val vector = vector("vector", 1536)
}

internal fun ResultRow.toStory() = Story(
    id = this[StoryTable.id].value,
    title = this[StoryTable.title],
    size = this[StoryTable.size],
    score = this[StoryTable.score],
    coherence = this[StoryTable.coherence],
    happenedAt = this[StoryTable.happenedAt].toInstantUtc()
)

internal val storyColumns = listOf(
    StoryTable.id,
    StoryTable.title,
    StoryTable.size,
    StoryTable.score,
    StoryTable.coherence,
    StoryTable.happenedAt,
)