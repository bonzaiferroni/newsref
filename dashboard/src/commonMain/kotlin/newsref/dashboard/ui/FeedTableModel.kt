package newsref.dashboard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import newsref.db.services.FeedService
import newsref.db.services.LeadService
import newsref.model.data.Feed
import kotlin.collections.plus

class FeedTableModel(
    private val feedService: FeedService = FeedService(),
    private val leadService: LeadService = LeadService(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(FeedTableState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(feedItems = feedService.readAll())
            while (true) {
                val additions = mutableMapOf<Int, Int?>()
                val leadCounts = mutableMapOf<Int, Int>()
                val previousCounts = _uiState.value.leadCounts
                for (feed in _uiState.value.feedItems) {
                    val leads = leadService.getLeadsFromFeed(feed.id)
                    val count = leads.size
                    val currentCount = previousCounts[feed.id] ?: 0
                    val addition = count - currentCount
                    additions[feed.id] = addition
                    leadCounts[feed.id] = count
                }
                _uiState.value = _uiState.value.copy(
                    leadCounts = leadCounts,
                    leadAdditions = _uiState.value.leadAdditions + additions
                )
            }
        }
    }
}

data class FeedTableState(
    val feedItems: List<Feed> = emptyList(),
    val leadCounts: Map<Int, Int> = emptyMap(),
    val leadAdditions: List<Map<Int, Int?>> = emptyList()
)