package newsref.db.services

import kotlinx.datetime.*
import newsref.db.*
import newsref.db.tables.*
import newsref.db.utils.*
import newsref.model.data.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.BatchUpdateStatement
import kotlin.time.Duration

private val console = globalConsole.getHandle("HostScoreService")

class HostScoreService : DbService() {
    suspend fun findScoreSignals() = dbQuery {
        HostTable.leftJoin(SourceTable)
            .select(HostTable.id, HostTable.core, SourceTable.score.sum())
            .groupBy(HostTable.id)
            .orderBy(SourceTable.score.sum(), SortOrder.DESC_NULLS_LAST)
            .map {
                HostScoreSignal(
                    id = it[HostTable.id].value,
                    core = it[HostTable.core],
                    score = it[SourceTable.score.sum()] ?: 0
                )
            }
    }

    suspend fun updateScores(signals: List<HostScoreSignal>) = dbQuery {
        signals.forEach { signal ->
            HostTable.update({ HostTable.id eq signal.id}) {
                it[score] = signal.score
            }
        }
    }
}

data class HostScoreSignal(
    val id: Int,
    val core: String,
    val score: Int,
)