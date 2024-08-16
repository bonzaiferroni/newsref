package streetlight.app.chop.extensions

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Instant.toLocalDateTime() = this.toLocalDateTime(TimeZone.currentSystemDefault())