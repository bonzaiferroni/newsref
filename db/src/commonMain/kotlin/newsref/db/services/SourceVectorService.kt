package newsref.db.services

import newsref.db.DbService
import newsref.db.core.SourceDistance
import newsref.db.core.cosineDistance
import newsref.db.globalConsole
import newsref.db.tables.SourceVectorTable
import newsref.db.tables.PageTable
import newsref.db.tables.VectorModelTable
import newsref.db.tables.SourceDistanceTable
import newsref.db.tables.distanceInfoColumns
import newsref.db.tables.fromModel
import newsref.db.tables.toDistanceInfo
import newsref.db.tables.toSource
import newsref.db.tables.toSourceDistance
import newsref.db.tables.toVectorModel
import org.jetbrains.exposed.sql.*

private val console = globalConsole.getHandle("VectorService")

class SourceVectorService : DbService() {
	suspend fun readOrCreateModel(modelName: String) = dbQuery {
		val id = VectorModelTable.readModelIdByName(modelName)
			?: VectorModelTable.insertAndGetId { it[VectorModelTable.name] = modelName }.value
		VectorModelTable.selectAll()
			.where { VectorModelTable.id.eq(id) }
			.first().toVectorModel()
	}

	suspend fun readModel(modelId: Int) = dbQuery {
		VectorModelTable.selectAll()
			.where { VectorModelTable.id.eq(modelId) }
			.firstOrNull()?.toVectorModel()
	}

	suspend fun readVector(sourceId: Long, modelId: Int) = dbQuery {
		SourceVectorTable.select(SourceVectorTable.vector)
			.where { SourceVectorTable.sourceId.eq(sourceId) and SourceVectorTable.modelId.eq(modelId)}
			.firstOrNull()?.let { it[SourceVectorTable.vector] }
	}

	suspend fun findNextJob() = dbQuery {
		PageTable.leftJoin(SourceVectorTable).select(PageTable.columns)
			.where { SourceVectorTable.id.isNull() and PageTable.contentCount.greater(EMBEDDING_MIN_WORDS) and
					PageTable.contentCount.less(EMBEDDING_MAX_WORDS) }
			.orderBy(PageTable.score, SortOrder.DESC_NULLS_LAST)
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

		SourceDistanceTable.batchInsert(distances) { this.fromModel(it) }

		console.log("Inserted ${distances.size} distances from origin: $sourceId")
	}

	suspend fun readDistances(sourceId: Long) = dbQuery {
		val fromOriginDistances = SourceDistanceTable.join(PageTable, JoinType.LEFT, SourceDistanceTable.targetId, PageTable.id)
			.select(distanceInfoColumns)
			.where { SourceDistanceTable.originId.eq(sourceId)}
			.map { it.toDistanceInfo() }
		val fromTargetDistances = SourceDistanceTable.join(PageTable, JoinType.LEFT, SourceDistanceTable.originId, PageTable.id)
			.select(distanceInfoColumns)
			.where { SourceDistanceTable.targetId.eq(sourceId)}
			.map { it.toDistanceInfo() }

		(fromOriginDistances + fromTargetDistances).sortedBy { it.distance }
	}

	suspend fun readGeneratedDistances(sourceIds: List<Long>) = dbQuery {
		SourceDistanceTable.select(SourceDistanceTable.columns)
			.where { SourceDistanceTable.targetId.inList(sourceIds) and SourceDistanceTable.originId.inList(sourceIds) }
			.map { it.toSourceDistance() }
	}

	suspend fun readDistances(sourceIds: List<Long>, modelId: Int) = dbQuery {
		val vectors = SourceVectorTable.select(SourceVectorTable.vector)
			.where { SourceVectorTable.sourceId.inList(sourceIds) and SourceVectorTable.modelId.eq(modelId) }
			.map { Pair(it[SourceVectorTable.sourceId].value, it[SourceVectorTable.vector]) }

		val map = mutableMapOf<Long, List<SourceDistance>>()
		for ((id, vector) in vectors) {
			val cosine = SourceVectorTable.vector.cosineDistance(vector).alias("cosine_distance")
			val distances = SourceVectorTable.select(cosine, SourceVectorTable.sourceId)
				.where { SourceVectorTable.sourceId.neq(id) and SourceVectorTable.sourceId.inList(sourceIds)}
				.map { SourceDistance(
					originId = id,
					targetId = it[SourceVectorTable.sourceId].value,
					modelId = modelId,
					distance = it[cosine],
				)}
			map[id] = distances
		}
		map
	}

	suspend fun findDistances(vector: FloatArray, modelId: Int) = dbQuery {
		val cosine = SourceVectorTable.vector.cosineDistance(vector).alias("cosine_distance")
		SourceVectorTable.select(cosine, SourceVectorTable.sourceId)
			.where { SourceVectorTable.modelId.eq(modelId) }
			.map { Pair(it[SourceVectorTable.sourceId].value, it[cosine])}
	}
}

internal fun VectorModelTable.readModelIdByName(name: String) = this.select(VectorModelTable.id)
	.where { VectorModelTable.name eq name }
	.firstOrNull()?.let { it[VectorModelTable.id].value }

const val EMBEDDING_MIN_WORDS = 100
const val EMBEDDING_MIN_CHARACTERS = 500
const val EMBEDDING_MAX_WORDS = 5000
const val EMBEDDING_MAX_CHARACTERS = 7200