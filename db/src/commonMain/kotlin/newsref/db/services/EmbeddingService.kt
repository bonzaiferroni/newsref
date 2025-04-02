package newsref.db.services

import newsref.db.DbService
import newsref.db.globalConsole
import newsref.db.tables.*
import org.jetbrains.exposed.sql.*

private val console = globalConsole.getHandle("EmbeddingService")

class EmbeddingService : DbService() {
    suspend fun readOrCreateModel(modelName: String, description: String) = dbQuery {
        val id = EmbeddingFamilyTable.readModelId(modelName, description)
            ?: EmbeddingFamilyTable.insertAndGetId {
                it[EmbeddingFamilyTable.model] = modelName
                it[EmbeddingFamilyTable.description] = description
            }.value
        EmbeddingFamilyTable.selectAll()
            .where { EmbeddingFamilyTable.id.eq(id) }
            .first().toEmbeddingFamily()
    }

    suspend fun readModel(modelId: Int) = dbQuery {
        EmbeddingFamilyTable.selectAll()
            .where { EmbeddingFamilyTable.id.eq(modelId) }
            .firstOrNull()?.toEmbeddingFamily()
    }

    suspend fun readEmbedding(pageId: Long, modelId: Int) = dbQuery {
        EmbeddingTable.select(EmbeddingTable.vector)
            .where { EmbeddingTable.pageId.eq(pageId) and EmbeddingTable.familyId.eq(modelId) }
            .firstOrNull()?.let { it[EmbeddingTable.vector] }
    }

    suspend fun findNextJob() = dbQuery {
        PageTable.leftJoin(EmbeddingTable).select(PageTable.columns)
            .where {
                EmbeddingTable.id.isNull() and PageTable.cachedWordCount.greater(EMBEDDING_MIN_WORDS) and
                        PageTable.cachedWordCount.less(EMBEDDING_MAX_WORDS)
            }
            .orderBy(PageTable.score, SortOrder.DESC_NULLS_LAST)
            .firstOrNull()?.toPage()
    }

    suspend fun insertEmbedding(pageId: Long, familyId: Int, vector: FloatArray) = dbQuery {
        EmbeddingTable.insert {
            it[EmbeddingTable.pageId] = pageId
            it[EmbeddingTable.familyId] = familyId
            it[EmbeddingTable.vector] = vector
        }
    }
}

internal fun EmbeddingFamilyTable.readModelId(model: String, description: String) = this.select(EmbeddingFamilyTable.id)
    .where { EmbeddingFamilyTable.model.eq(model) and EmbeddingFamilyTable.description.eq(description) }
    .firstOrNull()?.let { it[EmbeddingFamilyTable.id].value }

const val EMBEDDING_MIN_WORDS = 100
const val EMBEDDING_MIN_CHARACTERS = 500
const val EMBEDDING_MAX_WORDS = 5000
const val EMBEDDING_MAX_CHARACTERS = 7200