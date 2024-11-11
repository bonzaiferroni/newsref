package newsref.web.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

fun Instant.sinceDescription(): String {
	val now = Clock.System.now()
	val timeSince = (now - this)
	val label = if (timeSince < 1.hours) {
		val minutes = timeSince.inWholeMinutes
		"$minutes minutes${minutes.pluralize()}"
	} else if (timeSince < 24.hours) {
		val hours = timeSince.inWholeHours
		"$hours hour${hours.pluralize()}"
	} else if (timeSince < 800.days) {
		val days = timeSince.inWholeDays
		"$days day${days.pluralize()}"
	} else {
		"${(timeSince.inWholeDays / 365.2422).format()} years"
	}
	return label
}

fun Long.pluralize() = if (this == 1L) "" else "s"