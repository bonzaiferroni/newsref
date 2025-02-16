package newsref.db.tables

import kotlinx.serialization.json.Json
import newsref.db.utils.toInstantUtc
import newsref.db.utils.toLocalDateTimeUtc
import newsref.db.model.SourceCache
import newsref.model.dto.SourceCollection
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

// source cache
internal object SourceCacheTable : IntIdTable("source_cache") {
    val sourceId = reference("source_id", SourceTable, ReferenceOption.CASCADE)
    val score = integer("score")
    val createdAt = datetime("created_at")
    val json = json<SourceCollection>("source", Json.Default)
}

internal class SourceCacheRow(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SourceCacheRow>(SourceCacheTable)
    var source by SourceRow referencedOn SourceCacheTable.sourceId
    var score by SourceCacheTable.score
    var createdAt by SourceCacheTable.createdAt
    var json by SourceCacheTable.json
}

internal fun SourceCacheRow.toModel() = SourceCache(
    id = this.id.value,
    sourceId = this.source.id.value,
    score = this.score,
    createdAt = this.createdAt.toInstantUtc(),
    json = this.json,
)

internal fun SourceCacheRow.fromModel(sourceCache: SourceCache, sourceRow: SourceRow) {
    source = sourceRow
    score = sourceCache.score
    createdAt = sourceCache.createdAt.toLocalDateTimeUtc()
    json = sourceCache.json
}