package newsref.db.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Clock.Companion.nowToLocalDateTimeUTC() = Clock.System.now().toLocalDateTime(TimeZone.UTC)

fun Instant.Companion.tryParse(str: String) = try {
    Instant.parse(str)
} catch (e: Exception) {
    null
}