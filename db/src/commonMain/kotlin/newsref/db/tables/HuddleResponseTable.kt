package newsref.db.tables

import klutch.db.Aspect
import klutch.db.tables.UserTable
import newsref.db.model.HuddleResponse
import klutch.utils.toInstantUtc
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object HuddleResponseTable : LongIdTable("huddle_response") {
    val huddleId = reference("huddle_id", HuddleTable, ReferenceOption.CASCADE).index()
    val userId = reference("user_id", UserTable, ReferenceOption.CASCADE).index()
    val commentId = reference("comment_id", CommentTable, ReferenceOption.CASCADE).nullable().index()
    val response = text("response")
    val time = datetime("datetime")
}

object HuddleResponseAspect: Aspect<HuddleResponseAspect, HuddleResponse>(
    HuddleResponseTable,
    ResultRow::toHuddleResponse
) {
    val id = add(HuddleResponseTable.id)
    val huddleId = add(HuddleResponseTable.huddleId)
    val userId = add(HuddleResponseTable.userId)
    val commentId = add(HuddleResponseTable.commentId)
    val response = add(HuddleResponseTable.response)
    val time = add(HuddleResponseTable.time)
}

fun ResultRow.toHuddleResponse() = HuddleResponse(
    id = this[HuddleResponseTable.id].value,
    huddleId = this[HuddleResponseTable.huddleId].value,
    userId = this[HuddleResponseTable.userId].value,
    commentId = this[HuddleResponseTable.commentId]?.value,
    response = this[HuddleResponseTable.response],
    time = this[HuddleResponseTable.time].toInstantUtc()
)