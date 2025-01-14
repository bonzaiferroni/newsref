package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import newsref.dashboard.FeedRowRoute
import newsref.db.services.FeedService
import newsref.model.data.Feed

class FeedRowModel(
    route: FeedRowRoute,
    feedService: FeedService = FeedService(),
) : ScreenModel<FeedRowState>(FeedRowState(route.feedId)) {
    init {
        viewModelScope.launch {
            val feed = feedService.read(route.feedId)
            editState { it.copy(feed = feed) }
        }
    }
}

data class FeedRowState(
    val feedId: Int,
    val feed: Feed? = null
)