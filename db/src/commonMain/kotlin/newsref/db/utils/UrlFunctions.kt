package newsref.db.utils

import newsref.model.core.Url
import newsref.model.core.toCheckedUrl

internal fun String.toCheckedFromDb() = this.toCheckedUrl(emptySet(), emptySet())

fun Url.stripParams() = this.href.toCheckedUrl(setOf(), setOf())