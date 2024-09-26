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
    val outletId = reference("outlet_id", OutletTable)
    val sourceId = reference("source_id", SourceTable)
    val url = text("url")
    val attemptCount = integer("attempt_count")
    val attemptedAt = datetime("attempted_at")
}

class LeadEntity(id: EntityID<Int>): IntEntity(id) {
    companion object: EntityClass<Int, LeadEntity>(LeadTable)

    var outlet by OutletEntity referencedOn LeadTable.outletId
    var source by SourceEntity referencedOn LeadTable.sourceId

    var url by LeadTable.url
    var attemptCount by LeadTable.attemptCount
    var attemptedAt by LeadTable.attemptedAt
}

fun LeadEntity.toData() = Lead(
    id = this.id.value,
    outletId = this.outlet.id.value,
    sourceId = this.source.id.value,
    url = this.url,
    attemptCount = this.attemptCount,
    attemptedAt = this.attemptedAt.toInstant(UtcOffset.ZERO)
)

fun LeadEntity.fromData(lead: Lead, sourceEntity: SourceEntity, outletEntity: OutletEntity) {
    source = sourceEntity
    outlet = outletEntity
    url = lead.url
    attemptCount = lead.attemptCount
    attemptedAt = lead.attemptedAt.toLocalDateTime(TimeZone.UTC)
}