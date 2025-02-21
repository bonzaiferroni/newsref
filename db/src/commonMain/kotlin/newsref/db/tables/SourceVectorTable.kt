package newsref.db.tables

import newsref.db.core.*
import newsref.db.utils.*
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement

internal object SourceVectorTable : LongIdTable("source_vector") {
	val sourceId = reference("source_id", PageTable, ReferenceOption.CASCADE)
	val modelId = reference("model_id", VectorModelTable, ReferenceOption.CASCADE)
	val vector = vector("vector", 1536)
}

internal object VectorModelTable : IntIdTable("vector_model") {
	val name = text("name")
}

internal fun ResultRow.toVectorModel() = VectorModel(
	id = this[VectorModelTable.id].value,
	name = this[VectorModelTable.name]
)

internal object SourceDistanceTable : LongIdTable("source_distance") {
	val originId = reference("origin_id", PageTable, ReferenceOption.CASCADE)
	val targetId = reference("target_id", PageTable, ReferenceOption.CASCADE)
	val modelId = reference("model_id", VectorModelTable, ReferenceOption.CASCADE)
	val distance = float("distance")
}

internal fun ResultRow.toSourceDistance() = SourceDistance(
	originId = this[SourceDistanceTable.originId].value,
	targetId = this[SourceDistanceTable.targetId].value,
	modelId = this[SourceDistanceTable.modelId].value,
	distance = this[SourceDistanceTable.distance]
)

internal fun InsertStatement<*>.fromModel(data: SourceDistance) {
	this[SourceDistanceTable.originId] = data.originId
	this[SourceDistanceTable.targetId] = data.targetId
	this[SourceDistanceTable.modelId] = data.modelId
	this[SourceDistanceTable.distance] = data.distance
}

internal val distanceInfoColumns = listOf(
	SourceDistanceTable.modelId,
	SourceDistanceTable.distance,
	PageTable.id,
	PageTable.url,
	PageTable.title,
)

internal fun ResultRow.toDistanceInfo() = DistanceInfo(
	sourceId = this[PageTable.id].value,
	modelId = this[SourceDistanceTable.modelId].value,
	distance = this[SourceDistanceTable.distance],
	title = this[PageTable.title],
	url = this[PageTable.url].toCheckedFromTrusted(),
)