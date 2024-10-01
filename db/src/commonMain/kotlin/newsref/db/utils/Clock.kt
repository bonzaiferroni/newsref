package newsref.db.utils

import kotlinx.datetime.*

fun Clock.Companion.nowToLocalDateTimeUTC() = Clock.System.now().toLocalDateTime(TimeZone.UTC)

fun Instant.Companion.tryParse(str: String) = try {
    parse(str)
} catch (e: Exception) {
    try {
        LocalDateTime.parse(str).toInstant(TimeZone.UTC)
    } catch (_: Exception) {
        null
    }
}

fun String.tryParseInstantOrNull() = Instant.tryParse(this)