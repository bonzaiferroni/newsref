package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.services.FeedService
import newsref.db.services.LeadService
import newsref.model.core.toUrlOrNull
import newsref.model.data.Feed
import newsref.model.data.LeadInfo
import kotlin.time.Duration.Companion.minutes

class FeedItemModel(
    private val route: FeedItemRoute,
    private val feedService: FeedService = FeedService(),
    private val leadService: LeadService = LeadService(),
) : ScreenModel<FeedItemState>(FeedItemState(route.feedId)) {
    
    init {
        viewModelScope.launch {
           while (true) {
               refreshItem()
               delay(stateNow.nextRefresh - Clock.System.now())
           }
        }
    }

    private suspend fun refreshItem() {
        val feed = feedService.read(route.feedId)
        val leadInfos = leadService.getLeadsFromFeed(route.feedId).sortedByDescending { it.id }
        setState { it.copy(
            feed = feed,
            updatedFeed = feed,
            updatedHref = feed?.url.toString(),
            leadInfos = leadInfos,
            nextRefresh = Clock.System.now() + 1.minutes
        ) }
    }

    fun changeHref(value: String) {
        val updatedUrl = value.toUrlOrNull()
        setState { it.copy(
            updatedFeed = it.updatedFeed?.copy(url = updatedUrl ?: it.updatedFeed.url),
            updatedHref = value
        )}
    }

    fun changeSelector(value: String) {
        setState { it.copy(updatedFeed = it.updatedFeed?.copy(selector = value)) }
    }

    fun changeExternal(value: Boolean) {
        setState { it.copy(updatedFeed = it.updatedFeed?.copy(external = value)) }
    }

    fun changeTrackPosition(value: Boolean) {
        setState { it.copy(updatedFeed = it.updatedFeed?.copy(trackPosition = value)) }
    }

    fun updateItem() {
        val updatedFeed = stateNow.updatedFeed
        if (!stateNow.canUpdateItem || updatedFeed == null) return
        viewModelScope.launch {
            feedService.update(updatedFeed)
            refreshItem()
        }
    }

    fun deleteFeed(onDelete: () -> Unit) {
        viewModelScope.launch {
            feedService.delete(stateNow.feedId)
            onDelete()
        }
    }
}

data class FeedItemState(
    val feedId: Int,
    val feed: Feed? = null,
    val updatedFeed: Feed? = null,
    val updatedHref: String = "",
    val leadInfos: List<LeadInfo> = emptyList(),
    val nextRefresh: Instant = Instant.DISTANT_PAST
) {
    val canUpdateItem get() = feed != updatedFeed
}