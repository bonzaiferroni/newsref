package newsref.db.utils

import com.eygraber.uri.Url

fun String.toUrl() = Url.parse(this)
fun String.toUrlOrNull() = Url.parseOrNull(this)
