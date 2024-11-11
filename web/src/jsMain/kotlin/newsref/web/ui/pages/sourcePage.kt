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
	renderStore(model.state, { it.source }) { state ->
		val source = state.source
		if (source == null) {
			p("Source id not found: $id")
			return@renderStore
		}
		col(className = "prose prose-invert w-full") {
			h1(source.headline)
			source.image?.let { url ->
				detailRow("Image") {
					image(url, className = "w-full h-auto max-h-80")
				}
			}
			source.description?.let { description ->
				detailRow("Description") {
					p(description)
				}
			}
			source.note?.let { note ->
				detailRow("Note") {
					p(note.replace("\n", "<br>").replace(Regex("""(http[s]?://[^\s<]+)""")) { matchResult ->
						val url = matchResult.value
						"""<a href="$url">$url</a>"""
					}, true)
				}
			}
			if (source.inLinks.isNotEmpty()) {
				renderStore(model.state, { it.showMoreInbound }) { inboundState ->
					val inLinks = source.inLinks.let {
						if (inboundState.showMoreInbound) it else it.take(INITIAL_LINK_COUNT)
					}
					detailRow("Inbound Links") {
						div(className = "flex flex-col") {
							for (link in inLinks) {
								sourceLink(link)
							}
						}
						val moreCount = source.inLinks.size - inLinks.size
						if (moreCount > 0 || inboundState.showMoreInbound) {
							val buttonText = if (inboundState.showMoreInbound)
								"Show less" else "Show $moreCount more"
							button(buttonText, className = "mx-auto block").bindTo(model::toggleMoreInbound)
						}
					}
				}
			}
			if (source.outLinks.isNotEmpty()) {
				renderStore(model.state, { it.showMoreOutbound }) { outboundState ->
					val outLinks = source.outLinks.let {
						if (outboundState.showMoreOutbound) it else it.take(INITIAL_LINK_COUNT)
					}
					detailRow("Outbound Links") {
						for (link in outLinks) {
							sourceLink(link)
						}
						val moreCount = state.source.outLinks.size - outLinks.size
						if (moreCount > 0 || outboundState.showMoreOutbound) {
							val buttonText = if (outboundState.showMoreOutbound)
								"Show less" else "Show $moreCount more"
							button(buttonText, className = "mx-auto block").bindTo(model::toggleMoreOutbound)
						}
					}
				}

			}
		}
	}
	return null
}

const val INITIAL_LINK_COUNT = 3