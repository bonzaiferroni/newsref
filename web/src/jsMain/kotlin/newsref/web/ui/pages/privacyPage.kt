package newsref.web.ui.pages

import io.kvision.core.Container
import io.kvision.html.div
import io.kvision.html.h3
import io.kvision.html.p
import newsref.web.core.PortalEvents
import newsref.web.ui.components.col
import newsref.web.ui.components.row

fun Container.privacyPage(): PortalEvents? {
    col {
        col {
            h3("Privacy Policy")
            p {
                +"This is the privacy policy."
            }
        }
        row {
            div(className = "bg-primary p-3") {
                +"Column 1"
            }
            div(className = "bg-secondary p-3") {
                +"Column 2"
            }
            div(className = "bg-success p-3") {
                +"Column 3"
            }
        }

        row {
            div(className = "bg-danger text-white p-3") {
                +"Column 4 (Wide)"
            }
            div(className = "col-12 col-md-4 bg-warning text-white p-3") {
                +"Column 5 (Narrow)"
            }
        }
    }
    return null
}