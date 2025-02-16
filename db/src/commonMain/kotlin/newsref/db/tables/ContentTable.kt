package newsref.db.tables

import newsref.db.model.Content
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.*

internal object ContentTable : LongIdTable("content") {
    val text = text("text").uniqueIndex()
}

internal class ContentRow(id: EntityID<Long>) : LongEntity(id) {
    companion object : EntityClass<Long, ContentRow>(ContentTable)

    var text by ContentTable.text

    val sources by SourceRow via SourceContentTable
}

internal object SourceContentTable : LongIdTable("source_content") {
    val sourceId = reference("source_id", SourceTable, ReferenceOption.CASCADE)
    val contentId = reference("content_id", ContentTable, ReferenceOption.CASCADE)
}

internal fun ContentRow.toModel() = Content(
    id = this.id.value,
    text = this.text,
)

internal fun ContentRow.fromModel(content: String) {
    text = content
}

internal fun ResultRow.toContent() = Content(
    id = this[ContentTable.id].value,
    text = this[ContentTable.text],
)