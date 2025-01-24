package newsref.db.tables

import newsref.db.core.SourceDistance
import newsref.db.core.vector
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.statements.InsertStatement

internal object SourceVectorTable : LongIdTable("source_vector") {
	val sourceId = reference("source_id", SourceTable, ReferenceOption.CASCADE)
	val modelId = reference("model_id", VectorModelTable, ReferenceOption.CASCADE)
	val vector = vector("vector", 1536)
}

internal object VectorModelTable : IntIdTable("vector_model") {
	val name = text("name")
}

internal object SourceDistanceTable : LongIdTable("source_distance") {
	val originId = reference("origin_id", SourceTable, ReferenceOption.CASCADE)
	val targetId = reference("target_id", SourceTable, ReferenceOption.CASCADE)
	val modelId = reference("model_id", VectorModelTable, ReferenceOption.CASCADE)
	val distance = float("distance")
}

internal fun InsertStatement<*>.fromData(data: SourceDistance) {
	this[SourceDistanceTable.originId] = data.originId
	this[SourceDistanceTable.targetId] = data.targetId
	this[SourceDistanceTable.modelId] = data.modelId
	this[SourceDistanceTable.distance] = data.distance
}