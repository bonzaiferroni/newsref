package streetlight.web.pages

import io.kvision.html.Div
import io.kvision.html.link
import io.kvision.html.p
import io.kvision.panel.vPanel
import streetlight.model.dto.EventInfo
import streetlight.web.Layout
import streetlight.web.core.PortalEvents
import streetlight.web.gap
import streetlight.web.io.stores.EventStore
import streetlight.web.launchedEffect

fun Div.eventPage(): PortalEvents? {
    val store = EventStore()
    launchedEffect {
        try {
            val infos = store.getInfos()
            if (infos.isEmpty()) {
                p("No events found.")
            }
            // add elements to the page
            inflateInfos(store, infos)
        } catch (e: Exception) {
            p("(nope: $e)")
            console.log(e)
        }
    }
    return null
}

suspend fun Div.inflateInfos(store: EventStore, infos: List<EventInfo>) {
    vPanel {
        gap = Layout.defaultGap
        infos.forEach { info ->
            link(info.location.name, "#/event/${info.event.id}")
        }
    }
}