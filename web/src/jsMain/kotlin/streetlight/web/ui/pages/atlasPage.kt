package streetlight.web.ui.pages

import io.kvision.core.Container
import io.kvision.html.Div
import io.kvision.html.div
import io.kvision.html.p
import io.kvision.maps.Maps.Companion.L
import io.kvision.maps.externals.leaflet.geo.LatLng
import streetlight.web.core.AppContext
import streetlight.web.core.PortalEvents
import streetlight.web.launchedEffect
import streetlight.web.ui.components.col
import streetlight.web.ui.components.userContext
import streetlight.web.ui.models.AtlasModel

fun Container.atlasPage(context: AppContext): PortalEvents? {
    val model = AtlasModel()
    col("w-full") {
        val div = div(className = "w-full h-96 rounded")
        userContext(context) { userInfo ->
            launchedEffect {
                model.refresh()
                atlasWidget(context, model, div)
            }
        }
    }

    return null
}

fun Container.atlasWidget(context: AppContext, model: AtlasModel, div: Div) {
    val element = div.getElement()
    if (element != null) {
        val map = L.map(element).setView(LatLng(51.505, -0.09), 13)

        L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
            attribution =
                "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors"
        }).addTo(map)

        L.marker(LatLng(51.5, -0.09)).addTo(map)
            .bindPopup("'A pretty CSS popup.<br> Easily customizable.'")
            .openPopup()
    } else {
        p("Element not found")
    }
}