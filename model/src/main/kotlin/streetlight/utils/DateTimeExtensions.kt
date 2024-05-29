package streetlight.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun LocalDateTime.toEpochSeconds(): Long = toInstant(TimeZone.currentSystemDefault()).epochSeconds
fun Long.toLocalDateTime(): LocalDateTime = Instant.fromEpochSeconds(this)
    .toLocalDateTime(TimeZone.currentSystemDefault())
fun Instant.toLocalEpochSeconds(): Long = toLocalDateTime(TimeZone.currentSystemDefault())
    .toEpochSeconds()
fun Instant.toLocalDateTime(): LocalDateTime = toLocalDateTime(TimeZone.currentSystemDefault())