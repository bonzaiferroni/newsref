package newsref.db.services

import kotlinx.datetime.Instant
import klutch.db.DbService
import klutch.utils.greater
import newsref.db.tables.*
import org.jetbrains.exposed.sql.*

class HostDtoService : DbService() {

    suspend fun readHosts(ids: Collection<Int>) = dbQuery {
        HostDtoAspect.read { it.hostId.inList(ids) }
    }

    suspend fun readTopHosts(limit: Int = 20) = dbQuery {
        HostDtoAspect.readAll(HostDtoAspect.score, SortOrder.DESC_NULLS_LAST, limit)
    }

    suspend fun readHost(hostId: Int) = dbQuery {
        HostDtoAspect.readFirst { it.hostId.eq(hostId) }
    }

    suspend fun readSources(hostId: Int, start: Instant, limit: Int = 20) = dbQuery {
        PageLiteAspect.read(PageLiteAspect.feedPosition, SortOrder.ASC_NULLS_LAST, limit) {
            it.hostId.eq(hostId) and it.seenAt.greater(start)
        }
    }

    suspend fun readFeeds(core: String) = dbQuery {
        FeedDtoAspect.read { it.url.like("%$core%") }
    }

    suspend fun searchHosts(text: String) = dbQuery {
        HostDtoAspect.read { it.core.like("${text}%") or it.name.lowerCase().like("%${text.lowercase()}%") }
    }
}