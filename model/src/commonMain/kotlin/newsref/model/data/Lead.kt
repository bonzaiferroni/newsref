package newsref.model.data

import kotlinx.datetime.Instant
import newsref.model.core.CheckedUrl

data class Lead(
    val id: Long,
    val url: CheckedUrl,
    val targetId: Long? = null,
)

data class LeadJob(
    val id: Long = 0,
    val leadId: Long = 0,
    val feedId: Int? = null,
    val headline: String? = null,
)

data class LeadResult(
    val id: Long = 0,
    val leadId: Long,
    val outletId: Int,
    val result: ResultType,
    val attemptedAt: Instant,
)

data class LeadInfo(
    val id: Long,
    val url: CheckedUrl,
    val targetId: Long?,
    val outletId: Int?,
    val feedHeadline: String?,
    val attemptCount: Int,
    val lastAttemptAt: Instant?,
)

enum class ResultType {
    UNKNOWN,
    TIMEOUT,
    UNAUTHORIZED,
    BOT_DETECT,
    IRRELEVANT,
    RELEVANT
}