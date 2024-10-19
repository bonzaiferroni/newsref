package newsref.db.tables

import newsref.db.utils.toCheckedFromDb
import newsref.model.data.Link
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.count

internal object LinkTable: LongIdTable("link") {
    val sourceId = reference("source_id", SourceTable)
    val targetId = reference("target_id", SourceTable).nullable()
    val contentId = reference("content_id", ContentTable).nullable()
    val url = text("url")
    val urlText = text("url_text")
    val isExternal = bool("is_external")
}

internal class LinkRow(id: EntityID<Long>): LongEntity(id) {
    companion object: LongEntityClass<LinkRow>(LinkTable)

    var source by SourceRow referencedOn LinkTable.sourceId
    var target by SourceRow optionalReferencedOn LinkTable.targetId
    var content by ContentRow optionalReferencedOn LinkTable.contentId

    var url by LinkTable.url
    var urlText by LinkTable.urlText
    var isExternal by LinkTable.isExternal
}

internal fun LinkRow.toData() = Link(
    id = this.id.value,
    sourceId = this.source.id.value,
    targetId = this.target?.id?.value,
    contentId = this.content?.id?.value,
    url = this.url.toCheckedFromDb(),
    text = this.urlText,
    isExternal = this.isExternal
)

internal fun LinkRow.fromData(data: Link, sourceRow: SourceRow, contentRow: ContentRow?, targetRow: SourceRow?) {
    source = sourceRow
    content = contentRow
    targetRow?.let { target = it }
    url = data.url.toString()
    urlText = data.text
    isExternal = data.isExternal
}