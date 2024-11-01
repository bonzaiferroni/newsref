package newsref.web.ui.pages

import io.kvision.core.Container
import io.kvision.html.*
import newsref.model.dto.LinkInfo
import newsref.web.core.AppContext
import newsref.web.core.PortalEvents
import newsref.web.ui.components.col
import newsref.web.ui.components.renderStore
import newsref.web.ui.models.SourceModel
import newsref.web.ui.widgets.linkWidget
import newsref.web.utils.toAnchorString

fun Container.sourcePage(context: AppContext, id: Long): PortalEvents? {
	val model = SourceModel(id)
	renderStore(model.state, {it.source}) { state ->
		val source = state.source
		if (source == null) {
			p("Source id not found: $id")
			return@renderStore
		}
		col(className = "prose prose-invert w-full") {
			h1(source.headline)
			if (source.description != null) {
				h2("Description")
				p(source.description)
			}
			val inLinks = state.source.inLinks
			if (inLinks.isNotEmpty()) {
				h2("Inbound Links")
				listTag(ListType.UL) {
					for (link in inLinks) {
						linkWidget(link)
					}
				}
			}
			val outLinks = state.source.outLinks
			if (outLinks.isNotEmpty()) {
				h2("Outbound Links")
				listTag(ListType.UL) {
					for (link in outLinks) {
						linkWidget(link)
					}
				}
			}
		}
	}
	return null
}
