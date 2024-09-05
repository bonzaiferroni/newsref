package streetlight.web.content

import streetlight.web.*
import io.kvision.html.*
import io.kvision.panel.VPanel
import io.kvision.panel.hPanel
import io.kvision.panel.vPanel
import io.kvision.utils.perc
import streetlight.model.dto.EventInfo
import streetlight.web.components.card
import streetlight.web.io.EventStore

fun Div.eventProfile(id: Int) {
    val store = EventStore()
    val panel = vPanel(spacing = Constants.defaultGap) {
        hPanel(spacing = Constants.defaultGap) {
            link("back", "#/event/${id - 1}")
            link("next", "#/event/${id + 1}")
        }
    }
    launchedEffect {
        try {
            val info = store.getInfo(id)
            // add elements to the page
            panel.addElements(id, info, store)
        } catch (e: Exception) {
            p("(nope: $id)")
            console.log(e)
        }
    }
}

suspend fun VPanel.addElements(id: Int, info: EventInfo, store: EventStore) {
    h1(info.location.name)
    image("img/bridge.jpg") {
        width = 100.perc
    }
    p(info.event.description ?: description)
    card {
        p("this is a card")
    }
}