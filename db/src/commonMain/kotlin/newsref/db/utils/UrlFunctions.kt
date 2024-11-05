package newsref.db.utils

import newsref.model.core.CheckedUrl
import newsref.model.core.Url
import newsref.model.core.toCheckedUrl
import newsref.model.core.toUrl

internal fun String.toCheckedFromTrusted() = this.toCheckedUrl(emptySet(), emptySet())

fun Url.stripParams() = this.href.toCheckedUrl(setOf(), setOf())

fun Url.toNewDomain(domain: String) = "${this.scheme}://$domain${this.fullPath}".toUrl()

fun CheckedUrl.toNewDomain(domain: String) = "${this.scheme}://$domain${this.fullPath}".toCheckedFromTrusted()