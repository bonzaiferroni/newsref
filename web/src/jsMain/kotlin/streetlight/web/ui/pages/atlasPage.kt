package streetlight.web.ui.pages

import io.kvision.core.Container
import io.kvision.html.button
import io.kvision.maps.externals.leaflet.map.LeafletMap
import streetlight.web.core.AppContext
import streetlight.web.core.PortalEvents
import streetlight.web.launchedEffect
import streetlight.web.ui.components.*
import streetlight.web.ui.models.AtlasModel
import web.navigator.navigator

fun Container.atlasPage(context: AppContext): PortalEvents? {
    val model = AtlasModel()
    userContext(context) { userInfo ->
        launchedEffect {
            val map = leafletMap()
            model.refresh()
            atlasWidget(context, model, map)
        }
    }
    return null
}

fun Container.atlasWidget(context: AppContext, model: AtlasModel, map: LeafletMap) {

}