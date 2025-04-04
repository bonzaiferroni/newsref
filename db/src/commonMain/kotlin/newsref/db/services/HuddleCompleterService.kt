package newsref.db.services

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.DbService
import newsref.model.data.HuddleStatus
import newsref.db.model.Huddle
import newsref.db.utils.*
import newsref.db.tables.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class HuddleCompleterService(
    private val huddleAdapters: HuddleAdapterMap = globalHuddleAdapters
) : DbService() {

    suspend fun readActiveHuddles() = dbQuery {
        HuddleAspect.read { it.status.eq(HuddleStatus.Proposed) or it.status.eq(HuddleStatus.Extended) }
    }

    suspend fun readCompletedHuddles() = dbQuery {
        HuddleAspect.read { it.finishedAt.less(Clock.System.now()) and it.recordedAt.isNull()}
    }

    suspend fun readResponses(huddleId: Long) = dbQuery {
        HuddleResponseAspect.read { it.huddleId.eq(huddleId) }
    }

    suspend fun deleteHuddle(huddleId: Long) = dbQuery {
        HuddleTable.deleteWhere { id.eq(huddleId) }
    }

    suspend fun updateFinishedAt(huddleId: Long, time: Instant) = dbQuery {
        HuddleTable.update({ HuddleTable.id.eq(huddleId)}) {
            it[finishedAt] = time.toLocalDateTimeUtc()
            it[status] = HuddleStatus.Extended
        }
    }

    suspend fun completeHuddle(consensus: String?, huddle: Huddle) = dbQuery {
        val adapter = huddleAdapters.getValue(huddle.huddleType)
        consensus?.let {
            adapter.updateDatabase(it, huddle)
        }

        HuddleTable.update({ HuddleTable.id.eq(huddle.id)}) {
            it[HuddleTable.consensus] = consensus
            it[recordedAt] = Clock.System.now().toLocalDateTimeUtc()
            it[status] = when {
                consensus != null -> HuddleStatus.ConsensusReached
                else -> HuddleStatus.ConsensusFailed
            }
        }
    }
}