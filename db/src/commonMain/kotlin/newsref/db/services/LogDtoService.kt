package newsref.db.services

import klutch.db.DbService
import newsref.db.tables.LogDtoAspect
import newsref.db.tables.LogTable
import newsref.db.tables.toLogDto
import newsref.model.data.LogKey
import org.jetbrains.exposed.sql.SortOrder

class LogDtoService : DbService() {
    suspend fun readLogs(key: LogKey) = dbQuery {
        LogDtoAspect.where { it.pageId.eq(key.pageId) }
            .orderBy(LogTable.id, SortOrder.ASC)
            .map { it.toLogDto() }
    }
}