package newsref.web.ui.widgets

import io.kvision.core.Container
import io.kvision.html.p
import newsref.model.dto.LinkInfo
import newsref.web.ui.pages.getContextWithMarkup

fun Container.linkWidget(link: LinkInfo) {
	p(link.getContextWithMarkup(), rich = true)
}