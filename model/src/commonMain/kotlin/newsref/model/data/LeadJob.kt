package newsref.model.data

import kotlinx.datetime.Instant
import newsref.model.core.CheckedUrl

data class LeadJob(
	val id: Long = 0,
	val url: CheckedUrl,
	val feedId: Int?,
	val headline: String?,
	val attemptCount: Int = 0,
	val attemptedAt: Instant? = null,
)