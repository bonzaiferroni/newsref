package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import newsref.dashboard.ChapterItemRoute
import newsref.dashboard.StoryItemRoute
import newsref.db.services.*
import newsref.model.core.DataSort
import newsref.model.core.Sorting
import newsref.model.core.sortedByDirection
import newsref.model.data.*

class StoryItemModel(
    route: StoryItemRoute,
    storyService: StoryService = StoryService()
): StateModel<StoryItemState>(StoryItemState(route.storyId)) {
    init {
        viewModelScope.launch {
            val story = storyService.readStoryById(stateNow.storyId)
            val chapters = storyService.readStoryChapters(stateNow.storyId)
                .sortedByDescending { it.score }
                .toImmutableList()
            setState { it.copy(story = story, chapters = chapters)}
        }
    }
}

data class StoryItemState(
    val storyId: Long,
    val story: Story? = null,
    val chapters: ImmutableList<Chapter>? = null
)