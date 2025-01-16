package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import newsref.db.services.FeedService
import newsref.db.services.LeadService
import newsref.model.core.toUrl
import newsref.model.core.toUrlOrNull
import newsref.model.data.Feed
import kotlin.collections.plus
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class FeedTableModel(
    private val feedService: FeedService = FeedService(),
    private val leadService: LeadService = LeadService(),
) : ScreenModel<FeedTableState>(FeedTableState()) {

    init {
        viewModelScope.launch {
            while (true) {
                refresh()
                delay(1.minutes)
            }
        }
    }

    suspend fun refresh() {
        val items = feedService.readAll().sortedByDescending { it.createdAt }
        val additions = mutableMapOf<Int, Int?>()
        val previousCounts = stateNow.leadCounts
        val leadCounts = leadService.getAllFeedLeads().groupBy { it.feedId }
            .filterKeys { it != null }
            .mapKeys { it.key!! }
            .mapValues { it.value.size }
        for (feed in items) {
            val count = leadCounts[feed.id] ?: 0
            val currentCount = previousCounts[feed.id] ?: 0
            val addition = count - currentCount
            additions[feed.id] = addition
        }
        editState {
            it.copy(
                leadCounts = leadCounts,
                leadAdditions = it.leadAdditions + additions,
                items = items,
            )
        }
    }

    fun changeHref(value: String) {
        val updatedUrl = value.toUrlOrNull()
        editState {
            it.copy(
                newItem = it.newItem.copy(url = updatedUrl ?: it.newItem.url),
                newHref = value
            )
        }
    }

    fun changeSelector(value: String) {
        editState { it.copy(newItem = it.newItem.copy(selector = value)) }
    }

    fun changeExternal(value: Boolean) {
        editState { it.copy(newItem = it.newItem.copy(external = value)) }
    }

    fun changeTrackPosition(value: Boolean) {
        editState { it.copy(newItem = it.newItem.copy(trackPosition = value)) }
    }

    fun addNewItem() {
        if (!stateNow.canAddItem) return
        viewModelScope.launch {
            feedService.create(stateNow.newItem)
            editState { it.copy(newItem = emptyFeed) }
            refresh()
        }
    }
}

data class FeedTableState(
    val newItem: Feed = emptyFeed,
    val newHref: String = emptyFeed.url.href,
    val items: List<Feed> = emptyList(),
    val leadCounts: Map<Int, Int> = emptyMap(),
    val leadAdditions: List<Map<Int, Int?>> = emptyList()
) {
    val canAddItem get() = newHref.toUrlOrNull() != null && newItem.selector.isNotBlank()
}

private val emptyFeed = Feed(
    id = 0,
    url = "https://example.com/".toUrl(),
    selector = "",
    external = false,
    trackPosition = false,
    createdAt = Instant.DISTANT_PAST
)