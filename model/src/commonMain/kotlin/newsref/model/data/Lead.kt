package newsref.model.data

import kotlinx.datetime.Instant
import newsref.model.core.CheckedUrl

data class Lead(
	val id: Long = 0,
	val url: CheckedUrl,
	val hostId: Int = 0,
	val sourceId: Long? = null,
)

data class LeadResult(
	val id: Long = 0,
	val leadId: Long = 0,
	val result: FetchResult,
	val attemptedAt: Instant,
	val strategy: FetchStrategy?,
)

data class LeadJob(
	val id: Long = 0,
	val leadId: Long = 0,
	val feedId: Int? = null,
	val headline: String? = null,
	val isExternal: Boolean,
	val freshAt: Instant?,
	val feedPosition: Int? = null,
)

data class LeadInfo(
	val id: Long,
	val url: CheckedUrl,
	val hostId: Int,
	val targetId: Long?,
	val feedHeadline: String?,
	val feedPosition: Int?,
	val lastAttemptAt: Instant?,
	val isExternal: Boolean,
	val freshAt: Instant?,
	val linkCount: Int,
)

enum class FetchResult {
    UNKNOWN,
	ERROR,
	SKIPPED,
    TIMEOUT,
    UNAUTHORIZED,
    CAPTCHA,
    IRRELEVANT,
    RELEVANT
}

enum class FetchStrategy {
	BASIC,
	BROWSER,
}