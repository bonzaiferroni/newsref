package streetlight.web.ui.pages

import io.kvision.core.Container
import io.kvision.core.JustifyContent
import io.kvision.core.onClickLaunch
import io.kvision.form.text.text
import io.kvision.form.text.textArea
import io.kvision.html.ButtonStyle
import io.kvision.html.button
import io.kvision.html.h1
import io.kvision.html.p
import streetlight.web.core.AppContext
import streetlight.web.core.Pages
import streetlight.web.core.PortalEvents
import streetlight.web.core.navigate
import streetlight.web.launchedEffect
import streetlight.web.ui.components.*
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
    renderStore(model.state, { it.editMode }) { state ->
        if (state.editMode) {
            row(justify = JustifyContent.STRETCH) {
                text(value = state.song.name) { placeholder = "Name" }.bindTo(model::setName).grow()
                text(value = state.song.artist) { placeholder = "Artist" }.bindTo(model::setArtist).grow()
            }.expand()
            textArea(value = state.song.music) { placeholder = "Music" }.bindTo(model::setMusic).expand()
            row {
                button("Cancel", style = ButtonStyle.SECONDARY).bindToLaunch(model::toggleEditMode)
                safetyButton("Delete") {
                    model.deleteSong()
                    context.navigate(Pages.catalog)
                }
                button("Save").onClickLaunch {
                    model.updateSong()
                }
            }
        } else {
            h1(state.song.name)
            p(state.song.artist)
            p(state.song.music)
            row {
                button("Catalog", style = ButtonStyle.SECONDARY).onClick { context.navigate(Pages.catalog) }
                button("Edit").bindToLaunch(model::toggleEditMode)
            }
        }
    }

}