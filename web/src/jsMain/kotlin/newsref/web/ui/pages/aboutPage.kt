package newsref.web.ui.pages

import io.kvision.core.Container
import io.kvision.html.h3
import newsref.web.core.Pages
import newsref.web.core.PortalEvents
import newsref.web.ui.components.col
import newsref.web.ui.components.link

fun Container.aboutPage(): PortalEvents? {
    col {
        h3("About Streetlight")
        link("Privacy Policy! ", Pages.privacy)
    }
    return null
}

