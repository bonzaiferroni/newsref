package newsref.db.tables

import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import newsref.db.utils.toCheckedFromDb
import newsref.model.data.Source
import newsref.model.data.SourceType
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

internal object SourceTable : LongIdTable("source") {
    val outletId = reference("outlet_id", OutletTable)
    val url = text("url")
    val leadTitle = text("lead_title").nullable()
    val type = enumerationByName<SourceType>("source_type", 20)
    val attemptedAt = datetime("attempted_at")
}

internal class SourceRow(id: EntityID<Long>) : LongEntity(id) {
    companion object : EntityClass<Long, SourceRow>(SourceTable)

    var outlet by OutletRow referencedOn SourceTable.outletId
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

internal fun SourceRow.fromData(source: Source, outletRow: OutletRow) {
    outlet = outletRow
    url = source.url.toString()
    leadTitle = source.leadTitle
    type = source.type
    attemptedAt = source.attemptedAt.toLocalDateTime(TimeZone.UTC)
}

internal fun SourceRow.addContents(contentEntities: List<ContentRow>) {
    contents = SizedCollection(contentEntities)
}