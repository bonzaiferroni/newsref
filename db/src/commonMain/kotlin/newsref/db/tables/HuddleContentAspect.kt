package newsref.db.tables

import klutch.db.Aspect
import klutch.utils.toInstantUtc
import newsref.model.data.HuddleContentDto
import org.jetbrains.exposed.sql.ResultRow

object HuddleContentAspect : Aspect<HuddleContentAspect, HuddleContentDto>(
    HuddleTable,
    ResultRow::toHuddleContentDto
) {
    val huddleId = add(HuddleTable.id)
    val huddleType = add(HuddleTable.huddleType)
    val consensus = add(HuddleTable.consensus)
    val status = add(HuddleTable.status)
    val startedAt = add(HuddleTable.startedAt)
    val finishedAt = add(HuddleTable.finishedAt)
    val recordedAt = add(HuddleTable.recordedAt)
}

fun ResultRow.toHuddleContentDto() = HuddleContentDto(
    huddleId = this[HuddleTable.id].value,
    huddleType = this[HuddleTable.huddleType],
    consensus = this[HuddleTable.consensus],
    status = this[HuddleTable.status],
    startedAt = this[HuddleTable.startedAt].toInstantUtc(),
    finishedAt = this[HuddleTable.finishedAt].toInstantUtc(),
    recordedAt = this[HuddleTable.recordedAt]?.toInstantUtc(),
)