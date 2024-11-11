package newsref.db.tables

import newsref.db.core.Embedding
import newsref.db.core.vector
import newsref.db.tables.SourceTable.nullable
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

internal object EmbeddingTable : LongIdTable("embedding") {
	val sourceId = reference("source_id", SourceTable, ReferenceOption.CASCADE)
	val embedding = vector("embedding", 1536)
	val model = text("model")
}

internal class EmbeddingRow(id: EntityID<Long>) : LongEntity(id) {
	companion object : EntityClass<Long, EmbeddingRow>(EmbeddingTable)

	var source by SourceRow referencedOn EmbeddingTable.sourceId
	var embedding by EmbeddingTable.embedding
	var model by EmbeddingTable.model
}

internal fun EmbeddingRow.toData() = Embedding(
	id = id.value,
	sourceId = source.id.value,
	vector = embedding,
	model = model
)