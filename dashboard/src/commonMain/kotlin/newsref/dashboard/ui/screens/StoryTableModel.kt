package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import newsref.app.pond.core.StateModel
import newsref.dashboard.StoryTableRoute
import newsref.db.model.Story
import newsref.db.services.*
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