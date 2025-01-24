package newsref.db.services

import newsref.db.DbService
import newsref.db.core.SourceDistance
import newsref.db.core.cosineDistance
import newsref.db.globalConsole
import newsref.db.tables.SourceVectorTable
import newsref.db.tables.SourceTable
import newsref.db.tables.VectorModelTable
import newsref.db.tables.SourceDistanceTable
import newsref.db.tables.fromData
import newsref.db.tables.toSource
import org.jetbrains.exposed.sql.*

private val console = globalConsole.getHandle("VectorService")

class VectorService : DbService() {
	suspend fun findNextJob() = dbQuery {
		SourceTable.leftJoin(SourceVectorTable).select(SourceTable.columns)
			.where { SourceVectorTable.id.isNull() and SourceTable.contentCount.greater(500) and
					SourceTable.contentCount.less(5000) }
			.orderBy(SourceTable.score, SortOrder.DESC_NULLS_LAST)
			.firstOrNull()?.toSource()
	}

	suspend fun insertVector(sourceId: Long, modelName: String, vector: FloatArray) = dbQuery {
		val modelId = VectorModelTable.readModelIdByName(modelName)
			?: VectorModelTable.insertAndGetId { it[VectorModelTable.name] = modelName }.value

		SourceVectorTable.insert {
			it[SourceVectorTable.sourceId] = sourceId
			it[SourceVectorTable.modelId] = modelId
			it[SourceVectorTable.vector] = vector
		}
	}

	suspend fun generateDistances(sourceId: Long, modelName: String) = dbQuery {
		val modelId = VectorModelTable.readModelIdByName(modelName)
		if (modelId == null) throw IllegalStateException("model not found: $modelName")
		val vector = SourceVectorTable.select(SourceVectorTable.vector)
			.where { SourceVectorTable.sourceId.eq(sourceId) and SourceVectorTable.modelId.eq(modelId) }
			.firstOrNull()?.let { it[SourceVectorTable.vector] }
		if (vector == null) throw IllegalStateException()

		val cosineDistance = SourceVectorTable.vector.cosineDistance(vector).alias("cosine_distance")
		val distances = SourceVectorTable.select(SourceVectorTable.sourceId, SourceVectorTable.modelId, cosineDistance)
			.where { SourceVectorTable.modelId eq modelId }
			.map { SourceDistance(
				originId = sourceId,
				targetId = it[SourceVectorTable.sourceId].value,
				modelId = it[SourceVectorTable.modelId].value,
				distance = it[cosineDistance],
			)}

		SourceDistanceTable.batchInsert(distances) { this.fromData(it) }

		console.log("Inserted ${distances.size} distances from origin: $sourceId")
	}
}

internal fun VectorModelTable.readModelIdByName(name: String) = this.select(VectorModelTable.id)
	.where { VectorModelTable.name eq name }
	.firstOrNull()?.let { it[VectorModelTable.id].value }