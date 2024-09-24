package newsref.server.db.models

import newsref.model.core.IdModel

data class SessionToken(
    override val id: Int = 0,
    val userId: Int = 0,
    val token: String = "",
    val createdAt: Long = 0L,
    val expiresAt: Long = 0L,
    val issuer: String = "",
): IdModel