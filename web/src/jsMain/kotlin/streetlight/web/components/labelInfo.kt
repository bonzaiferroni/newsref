package streetlight.web.components

import io.kvision.core.Container
import io.kvision.html.div
import io.kvision.html.span

fun Container.labelInfo(label: String, info: String) {
    div() {
        span("$label: ", className = "text-muted")
        span(info)
    }
}