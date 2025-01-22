package newsref.web.ui.widgets

import io.kvision.core.Container
import newsref.model.dto.LinkCollection
import newsref.web.ui.css.*
import newsref.web.utils.replaceWithAnchor
import newsref.web.utils.sinceDescription

fun Container.sourceLink(link: LinkCollection) {
	div(className = "pb-4") {
		quote(link.context.replaceWithAnchor(link.urlText, link.url), rich = true)
		val authors = link.authors?.map{
			if (it.url != null) it.name.replaceWithAnchor(it.name, it.url!!) else it.name
		}?.joinListWithAnd()?.let { "$it, " } ?: ""

		div(row + justify_end + gap_1) {
			val attribution = "—${authors}${link.hostCore},".replaceWithAnchor(link.hostCore, link.sourceUrl)
			span(content = attribution, rich = true)
			val label = (link.publishedAt ?: link.seenAt).sinceDescription()
			span(text_muted, content = "from $label ago")
		}

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