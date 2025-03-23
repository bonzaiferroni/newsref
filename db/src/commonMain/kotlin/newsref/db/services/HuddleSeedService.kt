package newsref.db.services

import kotlinx.datetime.Clock
import newsref.db.DbService
import newsref.db.core.HuddleStatus
import newsref.db.model.SerializedHuddleOption
import newsref.db.tables.CommentTable
import newsref.db.tables.HuddleCommentTable
import newsref.db.tables.HuddleResponseTable
import newsref.db.tables.HuddleTable
import newsref.db.utils.nowToLocalDateTimeUtc
import newsref.db.utils.toLocalDateTimeUtc
import newsref.model.core.*
import newsref.model.data.HuddleSeed
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId

class HuddleSeedService : DbService() {
    suspend fun createHuddle(seed: HuddleSeed, sendingUserId: Long) = dbQuery {
        val (huddleOptions, duration, huddleGuide) = when (seed.type) {
            HuddleType.ChapterSourceRelevance -> TODO()
            HuddleType.CreateChapter -> TODO()
            HuddleType.EditArticleType -> Triple(
                first = editArticleTypeOptions.map { HuddleOption(it.label, it.value.name) },
                second = editArticleTypeDuration,
                third = editArticleTypeGuide
            )
        }

        val responseIndex = huddleOptions.indexOfFirst { it.value == seed.value }
        if (responseIndex < 0) error("Seed response not in huddle options")

        val newHuddleId = HuddleTable.insertAndGetId {
            it[chapterId] = seed.chapterId
            it[pageId] = seed.pageId
            it[initiatorId] = sendingUserId
            it[huddleType] = seed.type
            it[guide] = huddleGuide
            it[options] = huddleOptions.map { SerializedHuddleOption(it.label, it.value) }
            it[status] = HuddleStatus.Proposed
            it[startedAt] = Clock.nowToLocalDateTimeUtc()
            it[finishedAt] = (Clock.System.now() + duration).toLocalDateTimeUtc()
        }

        val newCommentId = CommentTable.insertAndGetId {
            it[userId] = sendingUserId
            it[text] = seed.comment
            it[time] = Clock.nowToLocalDateTimeUtc()
        }

        HuddleCommentTable.insert {
            it[commentId] = newCommentId.value
            it[huddleId] = newHuddleId.value
        }

        HuddleResponseTable.insert {
            it[huddleId] = newHuddleId.value
            it[userId] = sendingUserId
            it[commentId] = newCommentId.value
            it[time] = Clock.nowToLocalDateTimeUtc()
            it[response] = responseIndex
        }

        newHuddleId.value
    }
}