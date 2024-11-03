package newsref.web.ui.widgets

import io.kvision.core.Container
import io.kvision.html.div
import io.kvision.html.p
import newsref.model.dto.LinkInfo
import newsref.web.utils.replaceWithAnchor

fun Container.sourceLink(link: LinkInfo) {
	div(className = "pb-4") {
		quote(link.context.replaceWithAnchor(link.urlText, link.url), rich = true)
		val authors = link.authors?.map{
			if (it.url != null) it.name.replaceWithAnchor(it.name, it.url!!) else it.name
		}?.joinListWithAnd()?.let { "$it, " } ?: ""
		val attribution = "—${authors}${link.hostCore}".replaceWithAnchor(link.hostCore, link.sourceUrl)
		p(attribution, className = "text-right", rich = true)
	}
}

fun List<String>.joinListWithAnd(): String {
	return when (this.size) {
		0 -> ""
		1 -> this.first()
		2 -> this.joinToString(" and ")
		else -> this.dropLast(1).joinToString(", ") + ", and " + this.last()
	}
}