package newsref.db.tables

import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import newsref.model.data.Lead
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object LeadTable: IntIdTable("lead") {
    val sourceId = reference("source_id", SourceTable).nullable()
    val url = text("url")
    val attemptCount = integer("attempt_count")
    val attemptedAt = datetime("attempted_at").nullable()
}

class LeadRow(id: EntityID<Int>): IntEntity(id) {
    companion object: EntityClass<Int, LeadRow>(LeadTable)

    var source by SourceRow optionalReferencedOn LeadTable.sourceId

    var url by LeadTable.url
    var attemptCount by LeadTable.attemptCount
    var attemptedAt by LeadTable.attemptedAt
}

fun LeadRow.toData() = Lead(
    id = this.id.value,
    sourceId = this.source?.id?.value,
    url = this.url,
    attemptCount = this.attemptCount,
    attemptedAt = this.attemptedAt?.toInstant(UtcOffset.ZERO)
)

fun LeadRow.fromData(lead: Lead, sourceRow: SourceRow? = null) {
    source = sourceRow
    url = lead.url
    attemptCount = lead.attemptCount
    attemptedAt = lead.attemptedAt?.toLocalDateTime(TimeZone.UTC)
}