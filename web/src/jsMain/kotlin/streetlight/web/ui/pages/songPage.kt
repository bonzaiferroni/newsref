package streetlight.web.ui.pages

import io.kvision.core.Container
import io.kvision.core.onClickLaunch
import io.kvision.form.text.text
import io.kvision.html.ButtonStyle
import io.kvision.html.button
import io.kvision.html.h1
import io.kvision.html.p
import streetlight.web.core.AppContext
import streetlight.web.core.Pages
import streetlight.web.core.PortalEvents
import streetlight.web.core.navigate
import streetlight.web.launchedEffect
import streetlight.web.ui.components.bindTo
import streetlight.web.ui.components.renderStore
import streetlight.web.ui.components.row
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
    renderStore(model.state, { it.song }) { song ->
        h1(song.name)
        p(song.artist)
    }
    row {
        text(value = state.song.name) { placeholder = "Name" }.bindTo(model::setName)
        text(value = state.song.artist) { placeholder = "Artist" }.bindTo(model::setArtist)
    }
    row {
        button("Catalog", style = ButtonStyle.SECONDARY).onClick { context.navigate(Pages.catalog) }
        button("Delete", style = ButtonStyle.DANGER).onClickLaunch {
            model.deleteSong()
            context.navigate(Pages.catalog)
        }
        button("Save").onClickLaunch {
            model.updateSong()
        }
    }
}