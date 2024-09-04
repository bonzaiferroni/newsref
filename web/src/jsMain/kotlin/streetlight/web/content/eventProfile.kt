package streetlight.web.content

import io.kvision.html.Div
import io.kvision.html.link
import io.kvision.html.p
import io.kvision.panel.hPanel
import io.kvision.panel.vPanel
import streetlight.model.dto.EventInfo
import streetlight.web.Constants
import streetlight.web.io.EventStore
import streetlight.web.launchedEffect

fun Div.eventProfile(id: Int) {
    val store = EventStore()
    vPanel(spacing = Constants.defaultGap) {
        hPanel(spacing = Constants.defaultGap) {
            link("back", "#/event/${id - 1}")
            link("next", "#/event/${id + 1}")
        }
        launchedEffect {
            try {
                val info = store.getInfo(id)
                // add elements to the page
                addElements(id, info, store)
            } catch (e: Exception) {
                p("(nope: $id)")
                console.log(e)
            }
        }
    }
}

suspend fun Div.addElements(id: Int, info: EventInfo,  store: EventStore) {
    p(info.location.name)
}

//    div("event-image") {
//        // image(info.event.imageUrl ?: "static/img/bridge.jpg")
//        p(id.toString())
//    }
// }