package newsref.web.ui.pages

import io.kvision.core.Container
import io.kvision.html.*
import newsref.web.core.AppContext
import newsref.web.core.PortalEvents
import newsref.web.ui.components.col
import newsref.web.ui.components.detailRow
import newsref.web.ui.components.renderStore
import newsref.web.ui.models.SourceModel
import newsref.web.ui.widgets.sourceLink

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
				detailRow("Description") {
					p(source.description)
				}
			}
			val inLinks = state.source.inLinks
			if (inLinks.isNotEmpty()) {
				detailRow("Inbound Links") {
					div(className = "flex flex-col gap-4") {
						for (link in inLinks) {
							sourceLink(link)
						}
					}
				}
			}
			val outLinks = state.source.outLinks
			if (outLinks.isNotEmpty()) {
				detailRow("Outbound Links") {
					for (link in outLinks) {
						sourceLink(link)
					}
				}
			}
		}
	}
	return null
}
