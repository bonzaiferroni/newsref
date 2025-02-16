package newsref.app.ui

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.*
import kotlinx.coroutines.launch
import newsref.app.*
import newsref.app.blip.core.*
import newsref.app.io.*
import newsref.app.model.*

class ChapterFeedModel(
    val route: ChapterFeedRoute,
    val chapterStore: ChapterStore = ChapterStore()
) : StateModel<ChapterFeedState>(ChapterFeedState()) {
    init {
        viewModelScope.launch {
            val chapters = chapterStore.readChapters()
                .map { it.toModel() }
                .toImmutableList()
            setState { it.copy(chapterPacks = chapters)}
        }
    }
}

data class ChapterFeedState(
    val chapterPacks: ImmutableList<ChapterPack> = persistentListOf()
)

