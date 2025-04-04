package newsref.db.tables

import kotlinx.serialization.json.Json
import newsref.db.utils.toInstantUtc
import newsref.db.model.PageCache
import newsref.model.data.PageCollection
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

// source cache
internal object PageCacheTable : IntIdTable("page_cache") {
    val pageId = reference("page_id", PageTable, ReferenceOption.CASCADE)
    val score = integer("score")
    val createdAt = datetime("created_at")
    val json = json<PageCollection>("source", Json.Default)
}

internal fun ResultRow.toModel() = PageCache(
    id = this[PageCacheTable.id].value,
    pageId = this[PageCacheTable.pageId].value,
    score = this[PageCacheTable.score],
    createdAt = this[PageCacheTable.createdAt].toInstantUtc(),
    json = this[PageCacheTable.json],
)

//internal fun SourceCacheRow.fromModel(sourceCache: SourceCache, sourceRow: SourceRow) {
//    source = sourceRow
//    score = sourceCache.score
//    createdAt = sourceCache.createdAt.toLocalDateTimeUtc()
//    json = sourceCache.json
//}