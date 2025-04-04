package newsref.db.tables

import newsref.db.core.Aspect
import newsref.db.utils.toInstantUtc
import newsref.model.dto.LogDto
import org.jetbrains.exposed.sql.ResultRow

internal object LogDtoAspect: Aspect<LogDtoAspect, LogDto>(
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

internal fun ResultRow.toLogDto() = LogDto(
    this[LogTable.id].value,
    this[LogTable.pageId]?.value,
    this[LogTable.origin],
    this[LogTable.subject],
    this[LogTable.message],
    this[LogTable.time].toInstantUtc(),
)