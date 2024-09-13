package streetlight.web.ui.pages

import io.kvision.core.Container
import io.kvision.html.h3
import streetlight.web.core.Pages
import streetlight.web.core.PortalEvents
import streetlight.web.ui.components.link
import streetlight.web.ui.components.row

fun Container.aboutPage(): PortalEvents? {
    row {
        h3("About Streetlight")
        link("Privacy Policy! ", Pages.privacy)
    }
    return null
}

