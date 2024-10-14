package newsref.db.tables

import newsref.db.utils.toCheckedFromDb
import newsref.model.data.Link
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

internal object LinkTable: LongIdTable("link") {
    val sourceId = reference("source_id", SourceTable)
    val targetId = reference("target_id", SourceTable)
    val contentId = reference("content_id", ContentTable)
    val url = text("url")
    val urlText = text("url_text")
}

internal class LinkRow(id: EntityID<Long>): LongEntity(id) {
    companion object: LongEntityClass<LinkRow>(LinkTable)

    var source by SourceRow referencedOn LinkTable.sourceId
    var target by SourceRow referencedOn LinkTable.targetId
    var content by ContentRow referencedOn LinkTable.contentId

    var url by LinkTable.url
    var urlText by LinkTable.urlText
}

internal fun LinkRow.toData() = Link(
    id = this.id.value,
    sourceId = this.source.id.value,
    targetId = this.target.id.value,
    contentId = this.content.id.value,
    url = this.url.toCheckedFromDb(),
    text = this.urlText,
)

internal fun LinkRow.fromData(data: Link, sourceRow: SourceRow, contentRow: ContentRow, targetRow: SourceRow?) {
    source = sourceRow
    content = contentRow
    targetRow?.let { target = it }
    url = data.url.toString()
    urlText = data.text
}