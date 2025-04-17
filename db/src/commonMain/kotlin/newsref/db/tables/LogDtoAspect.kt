package newsref.db.tables

import klutch.db.Aspect
import klutch.utils.toInstantUtc
import newsref.model.data.Log
import org.jetbrains.exposed.sql.ResultRow

internal object LogDtoAspect: Aspect<LogDtoAspect, Log>(
    LogTable,
    ResultRow::toLogDto
) {
    val id = add(LogTable.id)
    val pageId = add(LogTable.pageId)
    val origin = add(LogTable.origin)
    val subject = add(LogTable.subject)
    val message = add(LogTable.message)
    val time = add(LogTable.time)
}

internal fun ResultRow.toLogDto() = Log(
    this[LogTable.id].value,
    this[LogTable.pageId]?.value,
    this[LogTable.origin],
    this[LogTable.subject],
    this[LogTable.message],
    this[LogTable.time].toInstantUtc(),
)