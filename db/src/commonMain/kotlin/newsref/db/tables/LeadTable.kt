package newsref.db.tables

import newsref.db.tables.ArticleRow.Companion.find
import newsref.db.utils.toCheckedFromDb
import newsref.model.core.CheckedUrl
import newsref.model.data.Lead
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.lowerCase

internal object LeadTable: LongIdTable("lead") {
    val url = text("url").uniqueIndex()
    val targetId = reference("target_id", SourceTable).nullable()
}

class LeadRow(id: EntityID<Long>): LongEntity(id) {
    companion object: EntityClass<Long, LeadRow>(LeadTable)
    var target by SourceRow optionalReferencedOn LeadTable.targetId
    var url by LeadTable.url
}

fun LeadRow.toData() = Lead(
    id = this.id.value,
    targetId = this.target?.id?.value,
    url = this.url.toCheckedFromDb(),
)

fun LeadRow.fromData(lead: Lead, sourceRow: SourceRow? = null) {
    target = sourceRow
    url = lead.url.toString()
}

fun LeadRow.Companion.leadExists(checkedUrl: CheckedUrl) =
    this.find { LeadTable.url.lowerCase() eq checkedUrl.toString().lowercase() }.any()