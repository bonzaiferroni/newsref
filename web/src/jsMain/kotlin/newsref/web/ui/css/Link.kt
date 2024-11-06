package newsref.web.ui.css

import io.kvision.core.ClassSetBuilder
import io.kvision.core.Container
import io.kvision.core.ResString
import io.kvision.html.Link

class Link(
	url: String? = null,
	var clasp: Clasp? = null,
	label: String = "",
	icon: String? = null,
	image: ResString? = null,
	separator: String? = null,
	labelFirst: Boolean = true,
	target: String? = null,
	dataNavigo: Boolean? = null,
	className: String? = null,
	init: (Link.() -> Unit)? = null
) : Link(label, url, icon, image, separator, labelFirst, target, dataNavigo, className, init) {
	override fun buildClassSet(classSetBuilder: ClassSetBuilder) {
		clasp?.build(classSetBuilder)
		super.buildClassSet(classSetBuilder)
	}
}

fun Container.link(
	url: String? = null,
	clasp: Clasp? = null,
	label: String = "",
	icon: String? = null,
	image: ResString? = null,
	separator: String? = null,
	labelFirst: Boolean = true,
	target: String? = null,
	dataNavigo: Boolean? = null,
	className: String? = null,
	init: (Link.() -> Unit)? = null
): Link {
	val link = Link(url, clasp, label, icon, image, separator, labelFirst, target, dataNavigo, className, init)
	this.add(link)
	return link
}
