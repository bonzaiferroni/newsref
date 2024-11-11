package newsref.web.ui.css

import io.kvision.core.ClassSetBuilder
import io.kvision.core.Container
import io.kvision.core.ResString
import io.kvision.html.Image
import io.kvision.html.ImageShape
import io.kvision.html.TAG

fun Container.icon(clasp: Clasp) = this.claspTag(TAG.SPAN, clasp = clasp)

fun Container.h1(content: String, clasp: Clasp? = null) = this.claspTag(TAG.H1, content = content, clasp = clasp)
fun Container.h2(content: String, clasp: Clasp? = null) = this.claspTag(TAG.H2, content = content, clasp = clasp)
fun Container.h3(content: String, clasp: Clasp? = null) = this.claspTag(TAG.H3, content = content, clasp = clasp)
fun Container.h4(content: String, clasp: Clasp? = null) = this.claspTag(TAG.H4, content = content, clasp = clasp)
fun Container.h5(content: String, clasp: Clasp? = null) = this.claspTag(TAG.H5, content = content, clasp = clasp)

class Image(
	src: ResString?,
	var clasp: Clasp? = null,
	alt: String? = null,
	responsive: Boolean = false,
	shape: ImageShape? = null,
	centered: Boolean = false,
	className: String? = null,
	init: (Image.() -> Unit)? = null
) : Image(src, alt, responsive, shape, centered, className, init) {
	override fun buildClassSet(classSetBuilder: ClassSetBuilder) {
		clasp?.build(classSetBuilder)
		super.buildClassSet(classSetBuilder)
	}
}

fun Container.image(
	src: ResString?,
	clasp: Clasp? = null,
	alt: String? = null,
	responsive: Boolean = false,
	shape: ImageShape? = null,
	centered: Boolean = false,
	className: String? = null,
	init: (Image.() -> Unit)? = null
): Image {
	val image = Image(src, clasp, alt, responsive, shape, centered, className, init)
	this.add(image)
	return image
}

