package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import newsref.dashboard.FeedRowRoute
import newsref.db.services.FeedService
import newsref.db.services.LeadService
import newsref.model.core.toUrlOrNull
import newsref.model.data.Feed
import newsref.model.data.LeadInfo

class FeedRowModel(
    private val route: FeedRowRoute,
    private val feedService: FeedService = FeedService(),
    private val leadService: LeadService = LeadService(),
) : ScreenModel<FeedRowState>(FeedRowState(route.feedId)) {
    
    init {
        viewModelScope.launch {
           refreshItem()
        }
    }

    private suspend fun refreshItem() {
        val feed = feedService.read(route.feedId)
        val leadInfos = leadService.getLeadsFromFeed(route.feedId).sortedBy { it.id }
        editState { it.copy(
            feed = feed,
            updatedFeed = feed,
            updatedHref = feed?.url.toString(),
            leadInfos = leadInfos
        ) }
    }

    fun changeHref(value: String) {
        val updatedUrl = value.toUrlOrNull()
        editState { it.copy(
            updatedFeed = it.updatedFeed?.copy(url = updatedUrl ?: it.updatedFeed.url),
            updatedHref = value
        )}
    }

    fun changeSelector(value: String) {
        editState { it.copy(updatedFeed = it.updatedFeed?.copy(selector = value)) }
    }

    fun changeExternal(value: Boolean) {
        editState { it.copy(updatedFeed = it.updatedFeed?.copy(external = value)) }
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

data class FeedRowState(
    val feedId: Int,
    val feed: Feed? = null,
    val updatedFeed: Feed? = null,
    val updatedHref: String = "",
    val leadInfos: List<LeadInfo> = emptyList(),
) {
    val canUpdateItem get() = feed != updatedFeed
}