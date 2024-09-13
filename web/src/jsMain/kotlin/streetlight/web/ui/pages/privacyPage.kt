package streetlight.web.ui.pages

import io.kvision.core.Container
import io.kvision.html.h3
import io.kvision.html.p
import streetlight.web.core.PortalEvents
import streetlight.web.ui.components.col
import streetlight.web.ui.components.row

fun Container.privacyPage(): PortalEvents? {
    col {
        row {
            h3("Privacy Policy")
            p {
                +"This is the privacy policy."
            }
        }
        row {
            col("bg-primary p-3") {
                +"Column 1"
            }
            col("bg-secondary p-3") {
                +"Column 2"
            }
            col("bg-success p-3") {
                +"Column 3"
            }
        }

        row {
            col("bg-danger text-white p-3") {
                +"Column 4 (Wide)"
            }
            col("col-12 col-md-4 bg-warning text-white p-3") {
                +"Column 5 (Narrow)"
            }
        }
    }
    return null
}