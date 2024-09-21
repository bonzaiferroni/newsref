package streetlight.web.ui.pages

import io.kvision.core.Container
import streetlight.web.core.AppContext
import streetlight.web.core.PortalEvents
import streetlight.web.launchedEffect
import streetlight.web.ui.components.userContext
import streetlight.web.ui.models.AtlasModel

fun Container.atlasPage(context: AppContext): PortalEvents? {
    val model = AtlasModel()
    userContext(context) { userInfo ->
        launchedEffect {
            model.refresh()

        }
    }
    return null
}