package newsref.db.tables

import newsref.db.core.*
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*

internal object EmbeddingTable : LongIdTable("embedding") {
	val pageId = reference("page_id", PageTable, ReferenceOption.CASCADE)
	val familyId = reference("family_id", EmbeddingFamilyTable, ReferenceOption.CASCADE)
	val vector = vector("vector", 768)
}

internal object EmbeddingFamilyTable : IntIdTable("embedding_family") {
	val model = text("model")
	val description = text("description")
}

internal fun ResultRow.toEmbeddingFamily() = EmbeddingFamily(
	id = this[EmbeddingFamilyTable.id].value,
	model = this[EmbeddingFamilyTable.model],
	description = this[EmbeddingFamilyTable.description],
)