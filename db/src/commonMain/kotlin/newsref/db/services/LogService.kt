package newsref.db.services

import newsref.db.DbService
import newsref.db.core.LogBook
import newsref.db.tables.LogTable
import newsref.db.utils.toLocalDateTimeUtc
import org.jetbrains.exposed.sql.batchInsert

class LogService : DbService() {

    suspend fun writeBook(logBook: LogBook, pageId: Long? = null) = dbQuery {
        val logs = logBook.finalize(pageId)
        LogTable.batchInsert(logs) {
            this[LogTable.pageId] = it.pageId
            this[LogTable.origin] = it.origin
            this[LogTable.subject] = it.subject
            this[LogTable.message] = it.message
            this[LogTable.time] = it.time.toLocalDateTimeUtc()
        }
    }
}