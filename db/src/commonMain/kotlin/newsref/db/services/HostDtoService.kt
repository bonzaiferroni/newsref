package newsref.db.services

import kotlinx.datetime.Instant
import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.utils.*
import newsref.model.core.*
import newsref.model.dto.*
import org.jetbrains.exposed.sql.*

class HostDtoService : DbService() {
    suspend fun readTopHosts(limit: Int = 20) = dbQuery {
        HostDtoAspect.readAll(HostDtoAspect.score, SortOrder.DESC_NULLS_LAST, limit)
    }
}