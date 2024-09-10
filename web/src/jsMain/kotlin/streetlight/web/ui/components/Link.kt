package streetlight.web.ui.components

import io.kvision.core.Container
import io.kvision.html.Link
import streetlight.web.core.PageConfig

fun Container.link(label: String, config: PageConfig): Link {
    return Link(label, config.linkRoute)
}