package newsref.model.data

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

enum class NewsSpan(val label: String, val duration: Duration) {
	DAY("Day", 1.days),
	WEEK("Week", 7.days),
	MONTH("Month", 30.days),
	YEAR("Year", 365.days);

	override fun toString(): String {
		return label
	}
}