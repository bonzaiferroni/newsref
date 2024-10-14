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
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

internal object SourceTable : LongIdTable("source") {
    val hostId = reference("outlet_id", HostTable)
    val url = text("url")
    val leadTitle = text("lead_title").nullable()
    val type = enumeration("source_type", SourceType::class).nullable()
    val attemptedAt = datetime("attempted_at")
}

internal class SourceRow(id: EntityID<Long>) : LongEntity(id) {
    companion object : EntityClass<Long, SourceRow>(SourceTable)

    var outlet by HostRow referencedOn SourceTable.hostId
    var contents by ContentRow via SourceContentTable

    var url by SourceTable.url
    var leadTitle by SourceTable.leadTitle
    var type by SourceTable.type
    var attemptedAt by SourceTable.attemptedAt

    val links by LinkRow referrersOn LinkTable.sourceId
    val document by ArticleRow referrersOn ArticleTable.sourceId
}

internal fun SourceRow.toData() = Source(
    id = this.id.value,
    url = this.url.toCheckedFromDb(),
    leadTitle = this.leadTitle,
    type = this.type,
    attemptedAt = this.attemptedAt.toInstant(UtcOffset.ZERO)
)

internal fun SourceRow.fromData(source: Source, hostRow: HostRow) {
    outlet = hostRow
    url = source.url.toString()
    source.leadTitle?.let { leadTitle = it }
    source.type?.let { type = it }
    attemptedAt = source.attemptedAt.toLocalDateTime(TimeZone.UTC)
}

internal fun SourceRow.addContents(contentEntities: List<ContentRow>) {
    contents = SizedCollection(contentEntities)
}