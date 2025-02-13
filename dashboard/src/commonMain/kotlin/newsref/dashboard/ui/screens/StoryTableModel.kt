package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import newsref.dashboard.StoryTableRoute
import newsref.db.services.*
import newsref.model.core.DataSort
import newsref.model.core.Sorting
import newsref.model.core.sortedByDirection
import newsref.model.data.*
import kotlin.time.Duration.Companion.minutes

class StoryTableModel(
    route: StoryTableRoute,
    val storyService: StoryService = StoryService()
): StateModel<StoryTableState>(StoryTableState(route.searchText ?: "")) {
    init {
        viewModelScope.launch {
            while (true) {
                val stories = storyService.readAllStories()
                    .sortedByDescending { it.score }
                setState { it.copy(stories = stories) }
                delay(1.minutes)
            }
        }
    }
}

data class StoryTableState(
    val searchText: String,
    val stories: List<Story> = emptyList()
)