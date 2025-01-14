package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import newsref.dashboard.FeedRowRoute
import newsref.db.services.FeedService
import newsref.model.core.toUrl
import newsref.model.core.toUrlOrNull
import newsref.model.data.Feed

class FeedRowModel(
    route: FeedRowRoute,
    feedService: FeedService = FeedService(),
) : ScreenModel<FeedRowState>(FeedRowState(route.feedId)) {
    init {
        viewModelScope.launch {
            val feed = feedService.read(route.feedId)
            editState { it.copy(
                feed = feed,
                updatedFeed = feed,
                updatedHref = feed?.url.toString()
            ) }
        }
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
}

data class FeedRowState(
    val feedId: Int,
    val feed: Feed? = null,
    val updatedFeed: Feed? = null,
    val updatedHref: String = "",
)