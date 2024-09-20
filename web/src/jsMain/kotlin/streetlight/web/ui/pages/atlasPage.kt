package streetlight.web.ui.pages

import io.kvision.core.Container
import streetlight.web.core.AppContext
import streetlight.web.core.PortalEvents
import streetlight.web.launchedEffect
import streetlight.web.ui.components.userContext

fun Container.atlasPage(context: AppContext): PortalEvents? {
    userContext(context) { userInfo ->
        launchedEffect {

        }
    }
    return null
}