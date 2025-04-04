package newsref.db.tables

import newsref.db.core.Aspect
import newsref.db.utils.toInstantUtc
import newsref.model.data.HuddleResponseDto
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow

object HuddleResponseDtoAspect : Aspect<HuddleResponseDtoAspect, HuddleResponseDto>(
    HuddleResponseTable.join(UserTable, JoinType.LEFT, HuddleResponseTable.userId, UserTable.id)
        .join(CommentTable, JoinType.LEFT, HuddleResponseTable.commentId, CommentTable.id),
    ResultRow::toHuddleResponseDto
) {
    val huddleId = add(HuddleResponseTable.huddleId)
    val responseId = add(HuddleResponseTable.id)
    val username = add(UserTable.username)
    val response = add(HuddleResponseTable.response)
    val comment = add(CommentTable.text)
    val time = add(HuddleResponseTable.time)
}

fun ResultRow.toHuddleResponseDto() = HuddleResponseDto(
    huddleId = this[HuddleResponseDtoAspect.huddleId].value,
    responseId = this[HuddleResponseDtoAspect.responseId].value,
    username = this[HuddleResponseDtoAspect.username],
    response = this[HuddleResponseDtoAspect.response],
    comment = this[HuddleResponseDtoAspect.comment],
    time = this[HuddleResponseDtoAspect.time].toInstantUtc(),
)