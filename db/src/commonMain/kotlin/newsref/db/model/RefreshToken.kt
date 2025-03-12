package newsref.db.model

import kotlinx.datetime.Clock
import newsref.db.utils.epochSecondsNow

data class RefreshToken(
    val id: Long,
    val userId: Long,
    val token: String,
    val createdAt: Long,
    val ttl: Int,
    val issuer: String,
) {
    val isExpired get() = Clock.epochSecondsNow() > createdAt + ttl
    val needsRotating get() = Clock.epochSecondsNow() > createdAt + ttl * REFRESH_TOKEN_ROTATION_FACTOR
}

const val REFRESH_TOKEN_ROTATION_FACTOR = .25f // 1/4 TTL