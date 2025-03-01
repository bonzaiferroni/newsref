package newsref.app.ui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import newsref.app.ChapterRoute
import newsref.app.blip.core.StateModel
import newsref.app.io.ChapterStore
import newsref.app.model.ChapterPack
import newsref.app.model.toModel

class ChapterModel(
    private val route: ChapterRoute,
    private val chapterStore: ChapterStore = ChapterStore()
) : StateModel<ChapterState>(ChapterState()) {
    init {
        viewModelScope.launch {
            val pack = chapterStore.readChapter(route.id).toModel()
            setState { it.copy(pack = pack) }
        }
    }
}

data class ChapterState(
    val pack: ChapterPack? = null
)