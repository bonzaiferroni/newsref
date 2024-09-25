package newsref.db.tables

import newsref.model.data.Link
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object LinkTable: LongIdTable("link") {
    val sourceId = reference("source_id", SourceTable)
    val url = text("url")
    val urlText = text("url_text")
    val context = text("context")
}

class LinkEntity(id: EntityID<Long>): LongEntity(id) {
    companion object: LongEntityClass<LinkEntity>(LinkTable)

    var source by SourceEntity referencedOn LinkTable.sourceId
    var url by LinkTable.url
    var urlText by LinkTable.urlText
    var context by LinkTable.context
}

fun LinkEntity.toData() = Link(
    id = this.id.value,
    sourceId = this.source.id.value,
    url = this.url,
    urlText = this.urlText,
    context = this.context,
)

fun LinkEntity.fromData(data: Link) {
    fromData(data, SourceEntity[data.sourceId])
}

fun LinkEntity.fromData(data: Link, source: SourceEntity) {
    this.source = source
    url = data.url
    urlText = data.urlText
    context = data.context
}