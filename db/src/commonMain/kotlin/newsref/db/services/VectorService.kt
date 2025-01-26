package newsref.db.services

import newsref.db.DbService
import newsref.db.core.SourceDistance
import newsref.db.core.cosineDistance
import newsref.db.globalConsole
import newsref.db.tables.SourceVectorTable
import newsref.db.tables.SourceTable
import newsref.db.tables.VectorModelTable
import newsref.db.tables.SourceDistanceTable
import newsref.db.tables.distanceInfoColumns
import newsref.db.tables.fromData
import newsref.db.tables.toDistanceInfo
import newsref.db.tables.toSource
import newsref.db.tables.toSourceDistance
import org.jetbrains.exposed.sql.*

private val console = globalConsole.getHandle("VectorService")

class VectorService : DbService() {
	suspend fun findNextJob() = dbQuery {
		SourceTable.leftJoin(SourceVectorTable).select(SourceTable.columns)
			.where { SourceVectorTable.id.isNull() and SourceTable.contentCount.greater(100) and
					SourceTable.contentCount.less(2000) }
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
			.where { SourceVectorTable.modelId.eq(modelId) and SourceVectorTable.sourceId.neq(sourceId) }
			.map { SourceDistance(
				originId = sourceId,
				targetId = it[SourceVectorTable.sourceId].value,
				modelId = it[SourceVectorTable.modelId].value,
				distance = it[cosineDistance],
			)}

		SourceDistanceTable.batchInsert(distances) { this.fromData(it) }

		console.log("Inserted ${distances.size} distances from origin: $sourceId")
	}

	suspend fun readDistances(sourceId: Long) = dbQuery {
		val fromOriginDistances = SourceDistanceTable.join(SourceTable, JoinType.LEFT, SourceDistanceTable.targetId, SourceTable.id)
			.select(distanceInfoColumns)
			.where { SourceDistanceTable.originId.eq(sourceId)}
			.map { it.toDistanceInfo() }
		val fromTargetDistances = SourceDistanceTable.join(SourceTable, JoinType.LEFT, SourceDistanceTable.originId, SourceTable.id)
			.select(distanceInfoColumns)
			.where { SourceDistanceTable.targetId.eq(sourceId)}
			.map { it.toDistanceInfo() }

		(fromOriginDistances + fromTargetDistances).sortedBy { it.distance }
	}
}

internal fun VectorModelTable.readModelIdByName(name: String) = this.select(VectorModelTable.id)
	.where { VectorModelTable.name eq name }
	.firstOrNull()?.let { it[VectorModelTable.id].value }