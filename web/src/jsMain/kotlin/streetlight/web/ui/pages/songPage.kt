package streetlight.web.ui.pages

import io.kvision.core.Container
import io.kvision.html.h1
import streetlight.web.core.AppContext
import streetlight.web.core.PortalEvents

fun Container.songPage(context: AppContext, id: Int): PortalEvents? {
    h1("Song $id")
    return null
}