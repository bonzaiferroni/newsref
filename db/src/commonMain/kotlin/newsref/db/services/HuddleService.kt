package newsref.db.services

import kotlinx.datetime.Clock
import newsref.db.DbService
import newsref.model.core.HuddleStatus
import newsref.db.tables.*
import newsref.db.utils.*
import newsref.model.data.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId

class HuddleService(
    val huddleAdapterMap: HuddleAdapterMap = globalHuddleAdapters
) : DbService() {

    suspend fun readPrompt(key: HuddleKey) = dbQuery {
        val adapter = huddleAdapterMap.getValue(key.type)
        val huddleOptions = adapter.readOptions(key)
        val huddleGuide = adapter.readGuide(key)
        val currentValue = adapter.readCurrentValue(key)
        HuddlePrompt(
            huddleGuide,
            huddleOptions,
            currentValue
        )
    }

    suspend fun createHuddle(seed: HuddleSeed, sendingUserId: Long) = dbQuery {
        val adapter = huddleAdapterMap.getValue(seed.key.type)

        val huddleOptions = adapter.readOptions(seed.key)
        val huddleGuide = adapter.readGuide(seed.key)
        val duration = adapter.duration

        val newHuddleId = HuddleTable.insertAndGetId {
            it[chapterId] = seed.key.chapterId
            it[pageId] = seed.key.pageId
            it[initiatorId] = sendingUserId
            it[huddleType] = seed.key.type
            it[guide] = huddleGuide
            it[options] = huddleOptions.map { HuddleOption(it.label, it.value) }
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
            it[response] = seed.value
        }

        newHuddleId.value
    }
}