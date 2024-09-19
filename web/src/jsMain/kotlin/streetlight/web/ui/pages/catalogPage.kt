package streetlight.web.ui.pages

import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.JustifyContent
import io.kvision.core.onClickLaunch
import io.kvision.form.text.text
import io.kvision.html.*
import streetlight.web.Icons
import streetlight.web.core.AppContext
import streetlight.web.core.Pages
import streetlight.web.core.PortalEvents
import streetlight.web.core.navigate
import streetlight.web.launchedEffect
import streetlight.web.ui.components.*
import streetlight.web.ui.models.CatalogModel

fun Container.catalogPage(context: AppContext): PortalEvents? {
    val model = CatalogModel()
    userContext(context) {
        launchedEffect {
            model.refresh()
            catalogWidget(context, model)
        }
    }
    return null
}

fun Container.catalogWidget(context: AppContext, model: CatalogModel) {
    col {
        h2("Catalog")

        row(alignItems = AlignItems.CENTER) {
            text() { placeholder = "Song" }.bindTo(model::setName)
            text() { placeholder = "Artist" }.bindTo(model::setArtist)
            button("Add").onClickLaunch {
                model.addSong()
            }
        }

        h3("Songs")
        renderStore(model.state, {it.songs}) { songs ->
            songs.forEach { song ->
                row(justify = JustifyContent.SPACEBETWEEN, alignItems = AlignItems.CENTER) {
                    p("${song.name} - ${song.artist}")
                    row {
                        iconButton(Icons.trash, ButtonStyle.DANGER)
                            .onClickLaunch { model.deleteSong(song) }
                        iconButton(Icons.edit, ButtonStyle.PRIMARY)
                            .onClick { context.navigate(Pages.song, song.id) }
                    }
                }.expand()
            }
        }
    }
}