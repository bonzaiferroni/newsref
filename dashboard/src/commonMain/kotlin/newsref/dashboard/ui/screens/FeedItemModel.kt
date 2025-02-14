package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.app.blip.core.StateModel
import newsref.dashboard.FeedItemRoute
import newsref.dashboard.utils.emptyImmutableList
import newsref.db.services.FeedService
import newsref.db.services.LeadService
import newsref.model.core.toUrlOrNull
import newsref.model.data.Feed
import newsref.model.data.LeadInfo
import newsref.model.dto.SourceInfo
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class FeedItemModel(
    private val route: FeedItemRoute,
    private val feedService: FeedService = FeedService(),
    private val leadService: LeadService = LeadService(),
) : StateModel<FeedItemState>(FeedItemState(route.feedId)) {
    
    init {
        viewModelScope.launch {
           while (true) {
               if (Clock.System.now() < stateNow.nextRefresh) {
                   delay(1.seconds)
                   continue
               }
               refreshItem()
           }
        }
    }

    private suspend fun refreshItem() {
        val feed = feedService.read(route.feedId)
        val leadInfos = leadService.getLeadsFromFeed(route.feedId)
            .sortedByDescending { it.id }
            .toImmutableList()
        val sourceInfos = feedService.readFeedSources(route.feedId)
            .groupBy { it.second.sourceId }.map { it.value.first() } // ensure unique sources
            .sortedBy { it.first }
            .map { it.second }
            .toImmutableList()
        setState { it.copy(
            feed = feed,
            updatedFeed = feed,
            updatedHref = feed?.url.toString(),
            leadInfos = leadInfos,
            sourceInfos = sourceInfos,
            nextRefresh = (feed?.checkAt ?: Clock.System.now()) + 1.minutes
        ) }
    }

    fun changeHref(value: String) {
        val updatedUrl = value.toUrlOrNull()
        setState { it.copy(
            updatedFeed = it.updatedFeed?.copy(url = updatedUrl ?: it.updatedFeed.url),
            updatedHref = value
        )}
    }

    fun changeUpdatedItem(item: Feed) { setState { it.copy(updatedFeed = item) } }

    fun updateItem() {
        var updatedFeed = stateNow.updatedFeed
        if (!stateNow.canUpdateItem || updatedFeed == null) return
        if (updatedFeed.debug) updatedFeed = updatedFeed.copy(checkAt = Clock.System.now())
        viewModelScope.launch {
            feedService.update(updatedFeed)
            refreshItem()
        }
    }

    fun checkFeed() {
        val feed = stateNow.feed
        if (feed == null) return
        changeUpdatedItem(feed.copy(checkAt = Clock.System.now()))
        updateItem()
    }

    fun changePage(page: String) { setState { it.copy(page = page) } }
}

data class FeedItemState(
    val feedId: Int,
    val feed: Feed? = null,
    val updatedFeed: Feed? = null,
    val updatedHref: String = "",
    val leadInfos: ImmutableList<LeadInfo> = emptyImmutableList(),
    val sourceInfos: ImmutableList<SourceInfo> = emptyImmutableList(),
    val nextRefresh: Instant = Instant.DISTANT_PAST,
    val page: String? = null,
) {
    val canUpdateItem get() = feed != updatedFeed
}