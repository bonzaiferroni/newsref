package newsref.db.utils

import newsref.db.core.CheckedUrl
import newsref.db.core.Url
import newsref.db.core.toCheckedUrl
import newsref.db.core.toUrl

internal fun String.toCheckedFromTrusted() = this.toCheckedUrl(emptySet(), emptySet())

fun Url.stripParams() = this.href.toCheckedUrl(setOf(), setOf())

fun Url.toNewDomain(domain: String) = "${this.scheme}://$domain${this.fullPath}".toUrl()

fun CheckedUrl.toNewDomain(domain: String) = "${this.scheme}://$domain${this.fullPath}".toCheckedFromTrusted()