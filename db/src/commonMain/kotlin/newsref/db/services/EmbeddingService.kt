package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.SourceRow
import newsref.db.tables.SourceTable
import newsref.db.tables.toData
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNotNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.and

class EmbeddingService : DbService() {
	suspend fun findNextJob() = dbQuery {
		SourceRow.find {
			SourceTable.embeddings.isNull() and SourceTable.wordCount.greater(0) and
					SourceTable.score.isNotNull()
		}
			.orderBy(Pair(SourceTable.score, SortOrder.DESC_NULLS_LAST))
			.firstOrNull()
			?.toData()
	}
}