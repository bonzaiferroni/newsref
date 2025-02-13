package newsref.model.utils

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

fun Duration.agoFormat() = when {
    this >= 365.days -> "${this.inWholeDays / 365}y"
    this >= 1.days -> "${this.inWholeDays}d"
    this >= 1.hours -> "${this.inWholeHours}h"
    this >= 1.minutes -> "${this.inWholeMinutes}m"
    this >= (-1).minutes -> "${this.inWholeSeconds}s"
    this >= (-1).hours -> "${this.inWholeMinutes}m"
    this >= (-1).days -> "${this.inWholeHours}h"
    this >= (-365).days -> "${this.inWholeDays}d"
    else -> "${this.inWholeDays / 365}y"
}
