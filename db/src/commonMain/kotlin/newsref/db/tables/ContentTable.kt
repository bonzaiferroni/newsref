package newsref.db.tables

import newsref.model.data.Content
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.CompositeIdTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object ContentTable : LongIdTable("content") {
    val text = text("text")
}

class ContentRow(id: EntityID<Long>) : LongEntity(id) {
    companion object : EntityClass<Long, ContentRow>(ContentTable)

    var text by ContentTable.text

    val sources by SourceRow via SourceContentTable
}

object SourceContentTable : CompositeIdTable("source_content") {
    val sourceId = reference("source_id", SourceTable)
    val contentId = reference("content_id", ContentTable)
    override val primaryKey = PrimaryKey(sourceId, contentId)
}

fun ContentRow.toData() = Content(
    id = this.id.value,
    text = this.text,
)

fun ContentRow.fromData(data: Content) {
    text = data.text
}