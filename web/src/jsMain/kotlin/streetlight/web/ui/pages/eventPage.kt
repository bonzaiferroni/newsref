package streetlight.web.ui.pages

import io.kvision.core.Container
import io.kvision.html.link
import io.kvision.html.p
import io.kvision.panel.vPanel
import streetlight.model.dto.EventInfo
import streetlight.web.Layout
import streetlight.web.core.Pages
import streetlight.web.core.PortalEvents
import streetlight.web.gap
import streetlight.web.io.stores.EventStore
import streetlight.web.launchedEffect

fun Container.eventPage(): PortalEvents? {
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

suspend fun Container.inflateInfos(store: EventStore, infos: List<EventInfo>) {
    vPanel {
        gap = Layout.defaultGap
        infos.forEach { info ->
            link(info.location.name, Pages.event.getIdRoute(info.event.id))
        }
    }
}