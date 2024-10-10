package newsref.server.db.services

import newsref.db.DataService
import newsref.db.models.SessionToken
import newsref.db.tables.SessionTokenEntity
import newsref.db.tables.SessionTokenTable
import newsref.db.tables.newFromData
import newsref.db.tables.toData

class SessionTokenService : DataService<SessionToken, Long, SessionTokenEntity>(
    SessionTokenEntity,
    {token -> token.id},
    SessionTokenEntity::newFromData,
    SessionTokenEntity::toData
) {
    suspend fun findByToken(token: String): SessionToken? = dbQuery {
        SessionTokenEntity.find { SessionTokenTable.token eq token }.firstOrNull()?.toData()
    }
}