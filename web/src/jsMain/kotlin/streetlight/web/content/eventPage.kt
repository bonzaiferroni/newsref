package streetlight.web.content

import io.kvision.core.onChangeLaunch
import io.kvision.html.Div
import io.kvision.html.link
import io.kvision.html.p
import io.kvision.panel.hPanel
import io.kvision.panel.vPanel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import streetlight.web.io.EventStore
import streetlight.web.utils.coScope
import streetlight.web.utils.launchedEffect

fun Div.eventPage(id: Int) {
    val eventStore = EventStore()
    vPanel(spacing = 10) {
        hPanel(spacing = 10) {
            link("back", "#/event/${id - 1}")
            link("next", "#/event/${id + 1}")
        }
        launchedEffect {
            try {
                val info = eventStore.getInfo(id)
                // add elements to the page
                p(info.location.name)
            } catch (e: Exception) {
                p("(nope: $id)")
                console.log(e)
            }
        }
    }
}

//    div("event-image") {
//        // image(info.event.imageUrl ?: "static/img/bridge.jpg")
//        p(id.toString())
//    }
// }