package newsref.db.tables

import newsref.db.utils.sameUrl
import newsref.db.utils.toCheckedFromTrusted
import newsref.model.core.CheckedUrl
import newsref.model.data.Link
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and

internal object LinkTable: LongIdTable("link") {
    val sourceId = reference("source_id", SourceTable, ReferenceOption.CASCADE).index()
    val leadId = reference("lead_id", LeadTable, ReferenceOption.SET_NULL).nullable().index()
    val contentId = reference("content_id", ContentTable, ReferenceOption.SET_NULL).nullable().index()
    val url = text("url")
    val urlText = text("url_text")
    val isExternal = bool("is_external")
}

internal class LinkRow(id: EntityID<Long>): LongEntity(id) {
    companion object: LongEntityClass<LinkRow>(LinkTable)

    var source by SourceRow referencedOn LinkTable.sourceId
    var lead by LeadRow optionalReferencedOn LinkTable.leadId
    var content by ContentRow optionalReferencedOn LinkTable.contentId

    var url by LinkTable.url
    var urlText by LinkTable.urlText
    var isExternal by LinkTable.isExternal
}

internal fun LinkRow.toData() = Link(
    id = this.id.value,
    sourceId = this.source.id.value,
    leadId = this.lead?.id?.value,
    contentId = this.content?.id?.value,
    url = this.url.toCheckedFromTrusted(),
    text = this.urlText,
    isExternal = this.isExternal
)

internal fun ResultRow.toLink() = Link(
    id = this[LinkTable.id].value,
    sourceId = this[LinkTable.sourceId].value,
    leadId = this[LinkTable.leadId]?.value,
    contentId = this[LinkTable.contentId]?.value,
    url = this[LinkTable.url].toCheckedFromTrusted(),
    text = this[LinkTable.urlText],
    isExternal = this[LinkTable.isExternal]
)

internal fun LinkRow.fromData(data: Link, sourceRow: SourceRow, contentRow: ContentRow?) {
    source = sourceRow
    content = contentRow
    url = data.url.toString()
    urlText = data.text
    isExternal = data.isExternal
}

internal fun LinkRow.Companion.setLeadOnSameLinks(url: CheckedUrl, leadRow: LeadRow): Boolean {
    val rows = LinkRow.find { LinkTable.url.sameUrl(url) and LinkTable.leadId.isNull() }
    rows.forEach {
        it.lead = leadRow
    }
    return rows.count() > 0
}