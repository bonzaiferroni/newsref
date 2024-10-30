package newsref.web.ui.pages

import io.kvision.core.Container
import io.kvision.html.h1
import io.kvision.html.h2
import io.kvision.html.link
import io.kvision.html.p
import newsref.web.core.AppContext
import newsref.web.core.PortalEvents
import newsref.web.ui.components.renderStore
import newsref.web.ui.models.SourceModel
import web.html.HTML.h1
import web.html.HTML.s

fun Container.sourcePage(context: AppContext, id: Long): PortalEvents? {
	val model = SourceModel(id)
	renderStore(model.state, {it.source}) { state ->
		val source = state.source
		if (source == null) {
			p("Source id not found: $id")
			return@renderStore
		}
		h1(source.headline)
//		if (source.description != null) {
//			p(article.description)
//		}
		val inLinks = state.source.inLinks
		if (inLinks.isNotEmpty()) {
			h2("Inbound Links")
			for (link in inLinks) {
				val text = link.context ?: link.urlText
				p(text)
				link(link.sourceUrl, link.sourceUrl)
			}
		} else {
			p("no inbound links")
		}
		val outLinks = state.source.outLinks
		if (outLinks.isNotEmpty()) {
			h2("Outbound Links")
			for (link in outLinks) {
				val text = link.context ?: link.urlText
				p(text)
				link(link.url, link.url)
			}
		} else {
			p("no outbound links")
		}
	}
	return null
}