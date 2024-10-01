package newsref.model.utils

import com.eygraber.uri.Uri
import com.eygraber.uri.Url

fun String.removeQueryParameters(keepParams: Set<String>): String {
    val parts = this.split('?', limit = 2)
    if (parts.size == 1) return this // No query params to strip

    val baseUrl = parts[0]
    val queryParams = parts[1]
        .split('&')
        .mapNotNull { param -> param.split('=', limit = 2).takeIf { it[0] in keepParams }?.joinToString("=") }
        .joinToString("&")

    return if (queryParams.isEmpty()) baseUrl else "$baseUrl?$queryParams"
}

fun String.tryParseUri(keepParams: Set<String> = emptySet(), context: Uri? = null): Uri? {
    val normalized = removeQueryParameters(keepParams).removePrefix("/")
    val uri = Uri.parseOrNull(normalized) ?: return null
    if (uri.isAbsolute) return Url.parseOrNull(this)
    if (uri.isRelative && context != null)
        return Uri.Builder()
            .scheme(context.scheme)
            .authority(context.authority)
            .path(uri.path)
            .fragment(uri.fragment)
            .build()
    return null
}

fun String.tryParseUrl(keepParams: Set<String> = emptySet(), context: Url? = null) = tryParseUri(keepParams, context)
    ?.let { Url.parseOrNull(it.toString()) }