package streetlight.web.ui.pages

import io.kvision.core.Container
import io.kvision.html.button
import io.kvision.html.h1
import io.kvision.html.p
import streetlight.web.core.AppContext
import streetlight.web.core.Pages
import streetlight.web.core.PortalEvents
import streetlight.web.core.navigate
import streetlight.web.launchedEffect
import streetlight.web.ui.components.col
import streetlight.web.ui.components.userContext
import streetlight.web.ui.models.SongModel

fun Container.songPage(context: AppContext, id: Int): PortalEvents? {
    val model = SongModel(id)
    userContext(context) {
        launchedEffect {
            model.refresh()
            songWidget(context, model)
        }
    }
    return null
}

fun Container.songWidget(context: AppContext, model: SongModel) {
    val state = model.state.value
    col {
        h1(state.song.name)
        p(state.song.artist)
        button("Catalog").onClick { context.navigate(Pages.catalog) }
    }
}