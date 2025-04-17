package newsref.db.services

import klutch.db.DbService
import newsref.db.*
import newsref.db.tables.*
import org.jetbrains.exposed.sql.*

private val console = globalConsole.getHandle("HostScoreService")

class HostScoreService : DbService() {
    suspend fun findScoreSignals() = dbQuery {
        HostTable.leftJoin(PageTable)
            .select(HostTable.id, HostTable.core, PageTable.score.sum())
            .groupBy(HostTable.id)
            .orderBy(PageTable.score.sum(), SortOrder.DESC_NULLS_LAST)
            .map {
                HostScoreSignal(
                    id = it[HostTable.id].value,
                    core = it[HostTable.core],
                    score = it[PageTable.score.sum()] ?: 0
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