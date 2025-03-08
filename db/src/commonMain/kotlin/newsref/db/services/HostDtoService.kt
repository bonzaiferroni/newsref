package newsref.db.services

import kotlinx.datetime.Instant
import newsref.db.DbService
import newsref.db.tables.*
import newsref.db.utils.*
import newsref.model.core.*
import newsref.model.dto.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.json.contains

class HostDtoService : DbService() {
    suspend fun readTopHosts(limit: Int = 20) = dbQuery {
        HostDtoAspect.readAll(HostDtoAspect.score, SortOrder.DESC_NULLS_LAST, limit)
    }

    suspend fun readHost(hostId: Int) = dbQuery {
        HostDtoAspect.readFirst { it.hostId.eq(hostId) }
    }

    suspend fun readSources(hostId: Int, start: Instant, limit: Int = 20) = dbQuery {
        SourceBitAspect.read(SourceBitAspect.feedPosition, SortOrder.ASC_NULLS_LAST, limit) {
            it.hostId.eq(hostId) and it.seenAt.greater(start)
        }
    }

    suspend fun readFeeds(core: String) = dbQuery {
        FeedDtoAspect.read { it.url.like("%$core%") }
    }
}