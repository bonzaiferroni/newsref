package newsref.db.utils

import kotlinx.datetime.*

fun Clock.Companion.nowToLocalDateTimeUTC() = Clock.System.now().toLocalDateTimeUTC()
fun Instant.toLocalDateTimeUTC() = toLocalDateTime(TimeZone.UTC).let { time ->
    LocalDateTime(
        year = time.year,
        monthNumber = time.monthNumber,
        dayOfMonth = time.dayOfMonth,
        hour = time.hour,
        minute = time.minute,
        second = time.second,
        nanosecond = 0
    )
}

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