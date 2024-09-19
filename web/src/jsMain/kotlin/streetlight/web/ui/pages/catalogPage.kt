package streetlight.web.ui.pages

import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.JustifyContent
import io.kvision.core.onClickLaunch
import io.kvision.form.text.text
import io.kvision.html.*
import streetlight.web.Icons
import streetlight.web.core.AppContext
import streetlight.web.core.PortalEvents
import streetlight.web.launchedEffect
import streetlight.web.ui.components.*
import streetlight.web.ui.models.CatalogModel

fun Container.catalogPage(context: AppContext): PortalEvents? {
    val model = CatalogModel()
    userContext(context) {
        launchedEffect {
            model.initialize()
            catalogWidget(model)
        }
    }
    return null
}

fun Container.catalogWidget(model: CatalogModel) {
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
        storeView(model.state, {it.songs}) { songs ->
            songs.forEach { song ->
                row(justify = JustifyContent.SPACEBETWEEN, alignItems = AlignItems.CENTER) {
                    p("${song.name} - ${song.artist}")
                    iconButton(Icons.trash, ButtonStyle.DANGER)
                        .onClickLaunch { model.deleteSong(song) }
                }
            }
        }
    }
}