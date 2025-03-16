package newsref.db.services

import newsref.db.DbService
import newsref.db.core.HuddleStatus
import newsref.db.model.*
import newsref.db.tables.*
import newsref.model.core.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class HuddleRunnerService : DbService() {

    suspend fun readActiveHuddles() = dbQuery {
        HuddleAspect.read { it.status.eq(HuddleStatus.Proposed) or it.status.eq(HuddleStatus.Extended) }
    }

    suspend fun readCompletedHuddles() = dbQuery {
        HuddleAspect.read { it.status.eq(HuddleStatus.ConsensusReached) and it.recordedAt.isNull()}
    }
}