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

data class LeadJob(
	val id: Long = 0,
	val leadId: Long = 0,
	val feedId: Int? = null,
	val headline: String? = null,
	val isExternal: Boolean,
	val freshAt: Instant?,
)

data class LeadInfo(
	val id: Long,
	val url: CheckedUrl,
	val hostId: Int,
	val targetId: Long?,
	val feedHeadline: String?,
	val attemptCount: Int,
	val lastAttemptAt: Instant?,
	val isExternal: Boolean,
	val freshAt: Instant?,
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