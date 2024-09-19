package streetlight.web.ui.pages

import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.onClickLaunch
import io.kvision.form.text.text
import io.kvision.html.button
import io.kvision.html.h2
import io.kvision.html.p
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
    storeView(model.state) {
        col {
            h2("Catalog")
            val songContainer = col()
            songContainer.songList(model)

            row(alignItems = AlignItems.CENTER) {
                text() { placeholder = "Song name" }.bindTo(model::setName)
                text() { placeholder = "Artist" }.bindTo(model::setArtist)
                button("Add").onClickLaunch {
                    model.addSong()
                    songContainer.removeAll()
                    songContainer.songList(model)
                }
            }
        }
    }
}

fun Container.songList(model: CatalogModel) {
    val state = model.state.value
    state.songs.forEach { song ->
        row {
            p(song.name)
            p(song.artist)
        }
    }
}