package newsref.db.services

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.DbService
import newsref.db.core.HuddleStatus
import newsref.db.utils.*
import newsref.db.tables.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class HuddleRunnerService : DbService() {

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

    suspend fun completeHuddle(huddleId: Long, consensus: Int?) = dbQuery {
        HuddleTable.update({ HuddleTable.id.eq(huddleId)}) {
            it[HuddleTable.consensus] = consensus
            it[recordedAt] = Clock.System.now().toLocalDateTimeUtc()
            it[status] = when {
                consensus != null -> HuddleStatus.ConsensusReached
                else -> HuddleStatus.ConsensusFailed
            }
        }
    }
}