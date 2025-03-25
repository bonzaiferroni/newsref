package newsref.model.utils

import kotlinx.datetime.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

fun Instant.formatSpanBrief() = (Clock.System.now() - this).formatSpanBrief()
fun Instant.formatSpanLong() = (Clock.System.now() - this).formatSpanLong()
fun Instant.untilFormat() = (this - Clock.System.now()).formatSpanBrief()
val Instant.epochDays get () = epochSeconds / 86_400.0
fun Instant.toDaysFromNow() = (this - Clock.System.now()).inWholeHours / 24f
fun Long.toInstantFromEpoch() = Instant.fromEpochSeconds(this)