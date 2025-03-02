package newsref.model.utils

import kotlinx.datetime.*

fun Instant.agoFormat() = (Clock.System.now() - this).agoFormat()
fun Instant.untilFormat() = (this - Clock.System.now()).agoFormat()
val Instant.epochDays get () = epochSeconds / 86_400.0
fun Instant.toDaysFromNow() = (this - Clock.System.now()).inWholeHours / 24f
fun Long.toInstantFromEpoch() = Instant.fromEpochSeconds(this)