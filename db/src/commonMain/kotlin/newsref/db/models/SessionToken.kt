package newsref.db.models

import java.util.*

data class SessionToken(
    val id: Long = 0,
    val userId: UUID = UUID.randomUUID(),
    val token: String = "",
    val createdAt: Long = 0L,
    val expiresAt: Long = 0L,
    val issuer: String = "",
)