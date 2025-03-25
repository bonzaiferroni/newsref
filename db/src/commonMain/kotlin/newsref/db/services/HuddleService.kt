package newsref.db.services

import kotlinx.datetime.Clock
import newsref.db.DbService
import newsref.model.core.HuddleStatus
import newsref.db.tables.*
import newsref.db.utils.*
import newsref.model.data.*
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
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
        val latestId = readLatestHuddleByKey(key)
        HuddlePrompt(
            guide = huddleGuide,
            options = huddleOptions,
            cachedValue = currentValue,
            activeId = latestId,
        )
    }

    fun readLatestHuddleByKey(key: HuddleKey) = HuddleTable.select(HuddleTable.id)
        .where {
            HuddleTable.targetId.eq(key.targetId) and
                    HuddleTable.chapterId.eq(key.chapterId) and
                    HuddleTable.pageId.eq(key.pageId) and
                    HuddleTable.huddleType.eq(key.type)
        }
        .orderBy(HuddleTable.startedAt, SortOrder.DESC)
        .firstOrNull()?.let { it[HuddleTable.id].value }

    suspend fun readUserResponseId(huddleId: Long, userId: Long) = dbQuery {
        HuddleResponseTable.select(HuddleResponseTable.id)
            .where { HuddleResponseTable.userId.eq(userId) and HuddleResponseTable.huddleId.eq(huddleId) }
            .orderBy(HuddleResponseTable.time, SortOrder.DESC)
            .firstOrNull()?.let { it[HuddleResponseTable.id].value }
    }

    suspend fun createHuddleResponse(seed: HuddleResponseSeed, sendingUserId: Long) = dbQuery {
        val adapter = huddleAdapterMap.getValue(seed.key.type)

        val huddleOptions = adapter.readOptions(seed.key)
        val huddleGuide = adapter.readGuide(seed.key)
        val duration = adapter.duration

        val readHuddleId = readLatestHuddleByKey(seed.key)?.takeIf {
            HuddleTable.select(HuddleTable.status)
                .where { HuddleTable.id.eq(it) }
                .first()[HuddleTable.status] < HuddleStatus.ConsensusReached
        } ?: HuddleTable.insertAndGetId {
            it[chapterId] = seed.key.chapterId
            it[pageId] = seed.key.pageId
            it[initiatorId] = sendingUserId
            it[huddleType] = seed.key.type
            it[guide] = huddleGuide
            it[options] = huddleOptions.map { HuddleOption(it.label, it.value) }
            it[status] = HuddleStatus.Proposed
            it[startedAt] = Clock.nowToLocalDateTimeUtc()
            it[finishedAt] = (Clock.System.now() + duration).toLocalDateTimeUtc()
        }.value

        val newCommentId = CommentTable.insertAndGetId {
            it[userId] = sendingUserId
            it[text] = seed.comment
            it[time] = Clock.nowToLocalDateTimeUtc()
        }

        HuddleCommentTable.insert {
            it[commentId] = newCommentId.value
            it[huddleId] = readHuddleId
        }

        val responseId = HuddleResponseTable.insertAndGetId {
            it[huddleId] = readHuddleId
            it[userId] = sendingUserId
            it[commentId] = newCommentId.value
            it[time] = Clock.nowToLocalDateTimeUtc()
            it[response] = seed.value
        }

        HuddleResponseDtoAspect.readFirst { it.responseId.eq(responseId) }
    }

    suspend fun readHuddleContent(huddleId: Long) = dbQuery {
        HuddleContentAspect.readFirst { it.huddleId.eq(huddleId) }
    }

    suspend fun readHuddleResponses(huddleId: Long) = dbQuery {
         HuddleResponseDtoAspect.read { HuddleResponseTable.huddleId.eq(huddleId) }

//        HuddleResponseTable.leftJoin(UserTable).leftJoin(CommentTable)
//            .select(HuddleResponseTable.id, HuddleResponseTable.huddleId, HuddleResponseTable.response, UserTable.username,
//                CommentTable.text, HuddleResponseTable.time)
//            .where { HuddleResponseTable.huddleId.eq(huddleId) }
//            .map { it.toHuddleResponseDto() }
    }
}