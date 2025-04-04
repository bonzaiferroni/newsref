package newsref.db.tables

import newsref.db.services.leftJoin
import newsref.db.tables.LinkTable.pageId
import newsref.db.tables.LinkTable.url
import newsref.db.tables.LinkTable.urlText
import newsref.db.utils.sameUrl
import newsref.db.utils.toCheckedFromTrusted
import newsref.db.utils.toInstantUtc
import newsref.db.core.CheckedUrl
import newsref.db.model.Link
import newsref.model.data.LinkInfo
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.update

internal object LinkTable : LongIdTable("link") {
    val pageId = reference("page_id", PageTable, ReferenceOption.CASCADE).index()
    val leadId = reference("lead_id", LeadTable, ReferenceOption.SET_NULL).nullable().index()
    val contentId = reference("content_id", ContentTable, ReferenceOption.SET_NULL).nullable().index()
    val url = text("url")
    val urlText = text("url_text")
    val textIndex = integer("text_index").default(-1)
    val isExternal = bool("is_external")
}

internal fun ResultRow.toLink() = Link(
    id = this[LinkTable.id].value,
    pageId = this[LinkTable.pageId].value,
    leadId = this[LinkTable.leadId]?.value,
    contentId = this[LinkTable.contentId]?.value,
    url = this[url].toCheckedFromTrusted(),
    text = this[urlText],
    textIndex = this[LinkTable.textIndex],
    isExternal = this[LinkTable.isExternal]
)

internal fun LinkTable.setLeadOnSameLinks(url: CheckedUrl, leadId: Long) =
    this.update({ LinkTable.url.sameUrl(url) and LinkTable.leadId.isNull() }) {
        it[LinkTable.leadId] = leadId
    } > 0

// link info

internal val linkInfoJoins
    get() =
        LinkTable.leftJoin(PageTable).leftJoin(HostTable)
            .leftJoin(LeadTable, LinkTable.leadId, LeadTable.id)
            .leftJoin(ContentTable)

internal val linkInfoColumns = listOf(
    LinkTable.pageId,
    LeadTable.pageId,
    LinkTable.url,
    LinkTable.urlText,
    LinkTable.textIndex,
    LinkTable.isExternal,
    ContentTable.text,
    PageTable.url,
    PageTable.seenAt,
    PageTable.publishedAt,
    PageTable.headline,
    HostTable.name,
    HostTable.core,
)

internal fun ResultRow.toLinkInfo() = LinkInfo(
    originId = this[pageId].value,
    targetId = this[LeadTable.pageId]?.value,
    url = this[url],
    urlText = this[urlText],
    textIndex = this[LinkTable.textIndex],
    isExternal = this[LinkTable.isExternal],
    context = this.getOrNull(ContentTable.text),
    originUrl = this[PageTable.url],
    hostName = this.getOrNull(HostTable.name),
    hostCore = this[HostTable.core],
    seenAt = this[PageTable.seenAt].toInstantUtc(),
    publishedAt = this[PageTable.publishedAt]?.toInstantUtc(),
    headline = this[PageTable.headline],
)