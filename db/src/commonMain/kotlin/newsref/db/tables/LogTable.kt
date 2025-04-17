package newsref.db.tables

import newsref.db.model.Log
import klutch.utils.toInstantUtc
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

internal object LogTable : LongIdTable("log") {
    val pageId = reference("page_id", PageTable, onDelete = ReferenceOption.CASCADE).nullable().index()
    val origin = text("origin")
    val subject = text("subject")
    val message = text("message")
    val time = datetime("time").index()
}

internal fun ResultRow.toLog() = Log(
    this[LogTable.id].value,
    this[LogTable.pageId]?.value,
    this[LogTable.origin],
    this[LogTable.subject],
    this[LogTable.message],
    this[LogTable.time].toInstantUtc(),
)

