package newsref.db.tables

import kotlinx.serialization.json.*
import newsref.db.core.Aspect
import newsref.db.core.HuddleStatus
import newsref.db.model.*
import newsref.db.utils.toInstantUtc
import newsref.model.core.HuddleType
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.json.jsonb
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object HuddleTable : LongIdTable("huddle") {
    val chapterId = reference("chapter_id", ChapterTable, ReferenceOption.CASCADE).nullable().index()
    val pageId = reference("source_id", PageTable, ReferenceOption.CASCADE).nullable().index()
    val initiatorId = reference("initiator_id", UserTable, ReferenceOption.CASCADE).index()
    val huddleType = enumeration<HuddleType>("huddle_type")
    val guide = text("guide")
    val options = jsonb<List<SerializedHuddleOption>>("options", Json.Default)
    val consensus = integer("consensus").nullable()
    val status = enumeration<HuddleStatus>("status")
    val startedAt = datetime("started_at")
    val finishedAt = datetime("finished_at")
    val recordedAt = datetime("recorded_at").nullable()
}

fun ResultRow.toHuddle() = Huddle(
    id = this[HuddleTable.id].value,
    chapterId = this[HuddleTable.chapterId]?.value ?: 0,
    pageId = this[HuddleTable.pageId]?.value ?: 0,
    initiatorId = this[HuddleTable.initiatorId].value,
    huddleType = this[HuddleTable.huddleType],
    guide = this[HuddleTable.guide],
    options = this[HuddleTable.options].toList(),
    consensus = this[HuddleTable.consensus],
    status = this[HuddleTable.status],
    startedAt = this[HuddleTable.startedAt].toInstantUtc(),
    finishedAt = this[HuddleTable.finishedAt].toInstantUtc(),
    recordedAt = this[HuddleTable.recordedAt]?.toInstantUtc(),
)

object HuddleAspect : Aspect<HuddleAspect, Huddle>(
    HuddleTable,
    ResultRow::toHuddle
) {
    val id = add(HuddleTable.id)
    val chapterId = add(HuddleTable.chapterId)
    val sourceId = add(HuddleTable.pageId)
    val initiatorId = add(HuddleTable.initiatorId)
    val huddleType = add(HuddleTable.huddleType)
    val guide = add(HuddleTable.guide)
    val options = add(HuddleTable.options)
    val consensus = add(HuddleTable.consensus)
    val status = add(HuddleTable.status)
    val startedAt = add(HuddleTable.startedAt)
    val finishedAt = add(HuddleTable.finishedAt)
    val recordedAt = add(HuddleTable.recordedAt)
}

object HuddleCommentTable : Table("huddle_comment") {
    val huddleId = reference("huddle_id", HuddleTable, ReferenceOption.CASCADE).index()
    val commentId = reference("comment_id", CommentTable, ReferenceOption.CASCADE).index()

    override val primaryKey = PrimaryKey(huddleId, commentId)
}