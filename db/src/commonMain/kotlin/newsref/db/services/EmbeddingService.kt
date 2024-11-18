package newsref.db.services

import newsref.db.DbService
import newsref.db.core.cosineDistanceNullable
import newsref.db.tables.SourceRow
import newsref.db.tables.SourceTable
import newsref.db.tables.toData
import org.jetbrains.exposed.sql.*

class EmbeddingService : DbService() {
	suspend fun findNextJob() = dbQuery {
		SourceRow.find {
			SourceTable.embedding.isNull() and SourceTable.contentCount.greater(0) and
					SourceTable.score.isNotNull()
		}
			.orderBy(Pair(SourceTable.score, SortOrder.DESC_NULLS_LAST))
			.firstOrNull()
			?.toData()
	}

	suspend fun setEmbedding(sourceId: Long, vector: FloatArray) = dbQuery {
		SourceRow[sourceId].embedding = vector
	}

	suspend fun getCosineNeighbors(sourceId: Long, limit: Int = 5) = dbQuery {
		val sourceRow = SourceRow[sourceId]
		val origin = sourceRow.embedding ?: throw IllegalStateException("source has no embedding")
		val cosineDistance = SourceTable.embedding.cosineDistanceNullable(origin).alias("cosine_distance")
		SourceTable.select(SourceTable.id, cosineDistance)
			.where { SourceTable.embedding.isNotNull() }
			.orderBy(cosineDistance, SortOrder.DESC)
			.limit(limit)
			.map { SourceNeighbor(
				originId = sourceId,
				neighborId = it[SourceTable.id].value,
				cosineDistance = it[cosineDistance]
			)}
	}
}

data class SourceNeighbor(
	val originId: Long,
	val neighborId: Long,
	val cosineDistance: Float,
)