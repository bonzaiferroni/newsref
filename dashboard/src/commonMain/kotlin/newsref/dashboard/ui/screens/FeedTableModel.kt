package newsref.dashboard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import newsref.db.services.FeedService
import newsref.db.services.LeadService
import newsref.model.data.Feed
import kotlin.collections.plus
import kotlin.time.Duration.Companion.seconds

class FeedTableModel(
    private val feedService: FeedService = FeedService(),
    private val leadService: LeadService = LeadService(),
) : ScreenModel<FeedTableState>(FeedTableState()) {

    init {
        viewModelScope.launch {
            while (true) {
                refresh()
                delay(30.seconds)
            }
        }
    }

    suspend fun refresh() {
        val feeds = feedService.readAll()
        val additions = mutableMapOf<Int, Int?>()
        val previousCounts = stateNow.leadCounts
        val leadCounts = leadService.getAllFeedLeads().groupBy { it.feedId }
            .filterKeys { it != null }
            .mapKeys { it.key!! }
            .mapValues { it.value.size }
        for (feed in feeds) {
            val count = leadCounts[feed.id] ?: 0
            val currentCount = previousCounts[feed.id] ?: 0
            val addition = count - currentCount
            additions[feed.id] = addition
        }
        editState {
            it.copy(
                leadCounts = leadCounts,
                leadAdditions = it.leadAdditions + additions,
                feedItems = feeds
            )
        }
    }
}

data class FeedTableState(
    val feedItems: List<Feed> = emptyList(),
    val leadCounts: Map<Int, Int> = emptyMap(),
    val leadAdditions: List<Map<Int, Int?>> = emptyList()
)