package newsref.web.ui.widgets

import io.kvision.core.Container
import io.kvision.html.div
import io.kvision.html.p
import newsref.model.dto.LinkInfo
import newsref.web.utils.replaceWithAnchor

fun Container.linkWidget(link: LinkInfo) {
	div {
		p(link.context.replaceWithAnchor(link.urlText, link.url), rich = true)
		p("— ${link.sourceUrl.replaceWithAnchor(link.sourceUrl, link.sourceUrl)}", rich = true)
	}
}