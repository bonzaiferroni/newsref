package newsref.db.tables

import newsref.db.model.Content
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.CompositeIdTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.*

internal object ContentTable : LongIdTable("content") {
    val text = text("text").uniqueIndex()
}

internal object PageContentTable : LongIdTable("page_content") {
    val pageId = reference("page_id", PageTable, ReferenceOption.CASCADE).index()
    val contentId = reference("content_id", ContentTable, ReferenceOption.CASCADE).index()
}

internal fun ResultRow.toContent() = Content(
    id = this[ContentTable.id].value,
    text = this[ContentTable.text],
)