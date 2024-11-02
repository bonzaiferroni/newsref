package newsref.web.ui.pages

import io.kvision.core.Container
import io.kvision.html.*
import newsref.web.core.AppContext
import newsref.web.core.PortalEvents
import newsref.web.ui.components.bindTo
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
			renderStore(model.state, { it.showMoreInbound }) { inboundState ->
				val inLinks = source.inLinks.let {
					if (inboundState.showMoreInbound) it else it.take(INITIAL_LINK_COUNT)
				}
				if (inLinks.isNotEmpty()) {
					detailRow("Inbound Links") {
						div(className = "flex flex-col gap-4") {
							for (link in inLinks) {
								sourceLink(link)
							}
						}
						val moreCount = source.inLinks.size - inLinks.size
						val buttonText = if (inboundState.showMoreInbound) "Show less" else "Show $moreCount more"
						button(buttonText, className = "mx-auto block").bindTo(model::toggleMoreInbound)
					}
				}
			}
			val outLinks = state.source.outLinks.let {
				if (state.showMoreOutbound) it else it.take(INITIAL_LINK_COUNT)
			}
			if (outLinks.isNotEmpty()) {
				detailRow("Outbound Links", true) {
					for (link in outLinks) {
						sourceLink(link)
					}
					val moreCount = state.source.outLinks.size - outLinks.size
					val buttonText = if (state.showMoreOutbound) "Show less" else "Show $moreCount more"
					button(buttonText, className = "mx-auto block").bindTo(model::toggleMoreOutbound)
				}
			}
		}
	}
	return null
}

const val INITIAL_LINK_COUNT = 3