package streetlight.web.ui.pages

import io.kvision.core.Container
import io.kvision.html.h3
import io.kvision.html.link
import streetlight.web.ui.components.rows
import streetlight.web.core.PortalEvents

fun Container.aboutPage(): PortalEvents? {
    rows {
        h3("About Streetlight")
        link("Privacy Policy", "#/privacy")
    }
    return null
}

