package newsref.web.ui.components

import io.kvision.core.Container
import io.kvision.html.Link
import io.kvision.html.link
import newsref.web.core.PageConfig

fun Container.link(label: String, config: PageConfig): Link {
    return link(label, config.linkRoute)
}