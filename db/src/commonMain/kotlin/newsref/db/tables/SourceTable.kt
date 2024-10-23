package newsref.db.tables

import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import newsref.db.utils.toCheckedFromDb
import newsref.model.core.SourceType
import newsref.model.data.Source
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

internal object SourceTable : LongIdTable("source") {
    val hostId = reference("host_id", HostTable)
    val url = text("url").uniqueIndex()
    val leadTitle = text("lead_title").nullable()
    val type = enumeration("source_type", SourceType::class).nullable()
    val seenAt = datetime("seen_at")
}

internal class SourceRow(id: EntityID<Long>) : LongEntity(id) {
    companion object : EntityClass<Long, SourceRow>(SourceTable)

    var host by HostRow referencedOn SourceTable.hostId
    var contents by ContentRow via SourceContentTable

    var url by SourceTable.url
    var leadTitle by SourceTable.leadTitle
    var type by SourceTable.type
    var seenAt by SourceTable.seenAt

    val links by LinkRow referrersOn LinkTable.sourceId
    val document by ArticleRow referrersOn ArticleTable.sourceId
    val linkScores by LinkScoreRow referrersOn LinkScoreTable.sourceId
}

internal fun SourceRow.toData() = Source(
    id = this.id.value,
    url = this.url.toCheckedFromDb(),
    title = this.leadTitle,
    type = this.type,
    seenAt = this.seenAt.toInstant(UtcOffset.ZERO)
)

internal fun ResultRow.toSource() = Source(
    id = this[SourceTable.id].value,
    url = this[SourceTable.url].toCheckedFromDb(),
    title = this[SourceTable.leadTitle],
    type = this[SourceTable.type],
    seenAt = this[SourceTable.seenAt].toInstant(UtcOffset.ZERO)
)

internal fun SourceRow.fromData(source: Source, hostRow: HostRow) {
    host = hostRow
    url = source.url.toString()
    source.title?.let { leadTitle = it }
    source.type?.let { type = it }
    seenAt = source.seenAt.toLocalDateTime(TimeZone.UTC)
}

internal fun SourceRow.addContents(contentEntities: List<ContentRow>) {
    contents = SizedCollection(contentEntities)
}