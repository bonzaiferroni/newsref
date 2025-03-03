package newsref.app.ui

import newsref.app.*
import newsref.app.blip.core.*
import newsref.app.model.ChapterSourcePack

class ChapterSourceModel(
    route: ChapterSourceRoute
) : StateModel<ChapterSourceState>(ChapterSourceState()) {
}

data class ChapterSourceState(
    val pack: ChapterSourcePack? = null
)