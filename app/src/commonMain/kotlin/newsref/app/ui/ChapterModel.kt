package newsref.app.ui

import newsref.app.ChapterRoute
import newsref.app.blip.core.StateModel

class ChapterModel(
    route: ChapterRoute,
) : StateModel<ChapterState>(ChapterState(route)) {
}

data class ChapterState(
    val route: ChapterRoute
)