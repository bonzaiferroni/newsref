package newsref.server.db.services

import newsref.db.DataService
import newsref.db.model.SessionToken
import newsref.db.tables.SessionTokenEntity
import newsref.db.tables.SessionTokenTable
import newsref.db.tables.fromModel
import newsref.db.tables.toModel

class SessionTokenService : DataService<SessionToken, Long, SessionTokenEntity>(
    SessionTokenEntity,
    {token -> token.id},
    SessionTokenEntity::fromModel,
    SessionTokenEntity::toModel
) {
    suspend fun findByToken(token: String): SessionToken? = dbQuery {
        SessionTokenEntity.find { SessionTokenTable.token eq token }.firstOrNull()?.toModel()
    }
}