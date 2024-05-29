package streetlight.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val id: Int = 0,
    val locationId: Int = 0,
    val startTime: Long = 0L,
    val endTime: Long = 0L,
) {
    fun startDateTime(): LocalDateTime {
        return Instant.fromEpochSeconds(startTime).toLocalDateTime(TimeZone.UTC)
    }

    fun endDateTime(): LocalDateTime {
        return Instant.fromEpochSeconds(endTime).toLocalDateTime(TimeZone.UTC)
    }
}