package newsref.db.tables

import newsref.model.data.Content
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.CompositeIdTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

internal object ContentTable : LongIdTable("content") {
    val text = text("text").uniqueIndex()
}

internal class ContentRow(id: EntityID<Long>) : LongEntity(id) {
    companion object : EntityClass<Long, ContentRow>(ContentTable)

    var text by ContentTable.text

    val sources by SourceRow via SourceContentTable
}

internal object SourceContentTable : CompositeIdTable("source_content") {
    val sourceId = reference("source_id", SourceTable)
    val contentId = reference("content_id", ContentTable)
    override val primaryKey = PrimaryKey(sourceId, contentId)
}

internal fun ContentRow.toData() = Content(
    id = this.id.value,
    text = this.text,
)

internal fun ContentRow.fromData(content: String) {
    text = content
}