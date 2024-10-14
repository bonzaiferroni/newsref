package newsref.model.data

import kotlinx.datetime.Instant
import newsref.model.core.CheckedUrl

data class Lead(
	val id: Long = 0,
	val url: CheckedUrl,
	val hostId: Int = 0,
	val targetId: Long? = null,
)

data class LeadResult(
    val id: Long = 0,
    val leadId: Long = 0,
    val result: ResultType,
    val attemptedAt: Instant,
)

data class FeedJob(
    val id: Long = 0,
	val feedId: Int = 0,
	val leadId: Long = 0,
    val headline: String? = null,
)

data class LeadInfo(
	val id: Long,
	val url: CheckedUrl,
	val hostId: Int,
	val targetId: Long?,
	val feedHeadline: String?,
	val attemptCount: Int,
	val lastAttemptAt: Instant?,
)

enum class ResultType {
    UNKNOWN,
	SKIPPED,
    TIMEOUT,
    UNAUTHORIZED,
    BOT_DETECT,
    IRRELEVANT,
    RELEVANT
}