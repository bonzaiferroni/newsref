package newsref.db.utils

import com.eygraber.uri.Uri
import com.eygraber.uri.Url

fun String.toUri() = Uri.parse(this)
fun String.toUriOrNull() = Uri.parseOrNull(this)

fun String.toTrustedUrl() = Url.parse(this)
