package newsref.db.tables

import kotlinx.datetime.*
import newsref.model.data.Document
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object DocumentTable: LongIdTable("document") {
    val sourceId = reference("source_id", SourceTable)
    val title = text("title")
    val description = text("description").nullable()
    val imageUrl = text("image_url").nullable()
    val accessedAt = datetime("accessed_at")
    val publishedAt = datetime("published_at").nullable()
    val modifiedAt = datetime("modified_at").nullable()
}

class DocumentRow(id: EntityID<Long>) : LongEntity(id) {
    companion object : EntityClass<Long, DocumentRow>(DocumentTable)

    var source by SourceRow referencedOn DocumentTable.sourceId

    var title by DocumentTable.title
    var description by DocumentTable.description
    var imageUrl by DocumentTable.imageUrl
    var accessedAt by DocumentTable.accessedAt
    var publishedAt by DocumentTable.publishedAt
    var modifiedAt by DocumentTable.modifiedAt
}

fun DocumentRow.toData() = Document(
    id = this.id.value,
    sourceId = this.source.id.value,
    title = this.title,
    description = this.description,
    imageUrl = this.imageUrl,
    accessedAt = this.accessedAt.toInstant(UtcOffset.ZERO),
    publishedAt = this.publishedAt?.toInstant(UtcOffset.ZERO),
    modifiedAt = this.modifiedAt?.toInstant(UtcOffset.ZERO)
)

fun DocumentRow.fromData(document: Document, sourceRow: SourceRow) {
    source = sourceRow
    title = document.title
    description = document.description
    imageUrl = document.imageUrl
    accessedAt = document.accessedAt.toLocalDateTime(TimeZone.UTC)
    publishedAt = document.publishedAt?.toLocalDateTime(TimeZone.UTC)
    modifiedAt = document.modifiedAt?.toLocalDateTime(TimeZone.UTC)
}