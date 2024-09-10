package streetlight.web.ui.pages

import io.kvision.core.Container
import io.kvision.html.h3
import io.kvision.html.p
import streetlight.web.ui.components.rows
import streetlight.web.core.PortalEvents

fun Container.privacyPage(): PortalEvents? {
    rows {
        h3("Privacy Policy")
        p {
            +"This is the privacy policy."
        }
    }
    return null
}