package newsref.web.utils

import io.kvision.html.Tag

fun Tag.tw(vararg classes: String): Tag {
	for (name in classes) this.addCssClass(name)
	return this
}

fun Tag.md(vararg classes: String): Tag {
	for (name in classes) this.addCssClass("md:$name")
	return this
}

fun <T: Tag> T.tw2(vararg classes: String) {
	for (name in classes) this.addCssClass(name)
}