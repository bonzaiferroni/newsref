package newsref.model.utils

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

fun Duration.formatSpanBrief() = when {
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

fun Duration.formatSpanLong() = when {
    this > 365.days -> "${this.inWholeDays / 365} years ago"
    this > 2.days -> "${this.inWholeDays} days ago"
    this > 1.days -> "1 day ago"
    this > 2.hours -> "${this.inWholeHours} hours ago"
    this > 1.hours -> "1 hour ago"
    this > 2.minutes -> "${this.inWholeMinutes} minutes ago"
    this > 1.minutes -> "1 minute ago"
    this > 0.seconds -> "${this.inWholeSeconds} seconds ago"
    this > (-2).minutes -> "in ${-this.inWholeSeconds} seconds"
    this > (-2).hours -> "in ${-this.inWholeMinutes} minutes"
    this > (-2).days -> "in ${-this.inWholeHours} hours"
    this > (-365).days -> "in ${-this.inWholeDays} days"
    else -> "in ${-this.inWholeDays / 365} years"
}
