package newsref.server.db.services

import newsref.db.DataService
import newsref.db.model.SessionToken
import newsref.db.tables.SessionTokenEntity
import newsref.db.tables.SessionTokenTable
import newsref.db.tables.fromData
import newsref.db.tables.toData

class SessionTokenService : DataService<SessionToken, Long, SessionTokenEntity>(
    SessionTokenEntity,
    {token -> token.id},
    SessionTokenEntity::fromData,
    SessionTokenEntity::toData
) {
    suspend fun findByToken(token: String): SessionToken? = dbQuery {
        SessionTokenEntity.find { SessionTokenTable.token eq token }.firstOrNull()?.toData()
    }
}