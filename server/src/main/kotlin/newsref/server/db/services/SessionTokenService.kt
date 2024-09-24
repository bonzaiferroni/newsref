package newsref.server.db.services

import newsref.server.db.DataService
import newsref.server.db.models.SessionToken
import newsref.server.db.tables.SessionTokenEntity
import newsref.server.db.tables.SessionTokenTable
import newsref.server.db.tables.fromData
import newsref.server.db.tables.toData

class SessionTokenService : DataService<SessionToken, SessionTokenEntity>(
    SessionTokenEntity,
    SessionTokenEntity::fromData,
    SessionTokenEntity::toData
) {
    suspend fun findByToken(token: String): SessionToken? = dbQuery {
        SessionTokenEntity.find { SessionTokenTable.token eq token }.firstOrNull()?.toData()
    }
}