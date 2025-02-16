package newsref.app.ui

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.*
import kotlinx.coroutines.launch
import newsref.app.*
import newsref.app.blip.core.*
import newsref.app.io.*
import newsref.model.data.Chapter

class ChapterFeedModel(
    val route: ChapterFeedRoute,
    val chapterStore: ChapterStore = ChapterStore()
) : StateModel<ChapterFeedState>(ChapterFeedState()) {
    init {
        viewModelScope.launch {
            val chapters = chapterStore.readChapters().toImmutableList()
            setState { it.copy(chapters = chapters)}
        }
    }
}

data class ChapterFeedState(
    val chapters: ImmutableList<Chapter> = persistentListOf()
)

