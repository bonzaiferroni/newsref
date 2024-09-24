package streetlight.web.ui.pages

import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.form.text.text
import io.kvision.html.button
import io.kvision.html.h3
import io.kvision.html.p
import streetlight.model.dto.UserInfo
import streetlight.web.Icons
import streetlight.web.core.AppContext
import streetlight.web.core.PortalEvents
import streetlight.web.launchedEffect
import streetlight.web.ui.components.*
import streetlight.web.ui.models.AtlasModel

fun Container.atlasPage(context: AppContext): PortalEvents? {
    val model = AtlasModel()
    userContext(context) { userInfo ->
        launchedEffect {
            val map = streetMap()
            model.refresh()
            map.awaitInflate()
            atlasWidget(context, model, userInfo, map)
        }
    }
    return null
}

fun Container.atlasWidget(context: AppContext, model: AtlasModel, userInfo: UserInfo, map: StreetMap) {
    col {
        h3("New Location")
        row(alignItems = AlignItems.CENTER) {
            text { placeholder = "Name" }.bindTo(model::setName)
            text { placeholder = "Place" }.bindTo(model::setPlace).bindFrom(model.state) { it.place }
            button("", Icons.search.css)
        }.expand()
        p("Click on the map to set the location")
    }
}