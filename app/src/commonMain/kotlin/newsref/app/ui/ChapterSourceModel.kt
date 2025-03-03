package newsref.app.ui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import newsref.app.*
import newsref.app.blip.controls.BalloonsData
import newsref.app.blip.core.*
import newsref.app.io.ChapterStore
import newsref.app.model.*

class ChapterSourceModel(
    route: ChapterSourceRoute,
    store: ChapterStore = ChapterStore()
) : StateModel<ChapterSourceState>(ChapterSourceState()) {
    init {
        viewModelScope.launch {

        }
    }
}

data class ChapterSourceState(
    val sourcePack: ChapterSourcePack? = null,
    val chapterPack: ChapterPack? = null,
    val balloonsData: BalloonsData? = null
)