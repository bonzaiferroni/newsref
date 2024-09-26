package newsref.db.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Clock.Companion.nowToLocalDateTimeUTC() = Clock.System.now().toLocalDateTime(TimeZone.UTC)