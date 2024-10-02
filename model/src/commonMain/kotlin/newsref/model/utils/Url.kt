package newsref.model.utils

import com.eygraber.uri.Uri
import com.eygraber.uri.Url
import newsref.model.data.Outlet

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

fun String.tryParseUntrustedUrl(keepParams: Set<String>?, context: Url?): Url? {
	val normalized = keepParams?.let { this.removeQueryParameters(it).removePrefix("/") } ?: this
	val uri = Uri.parseOrNull(normalized) ?: return null
	if (uri.isAbsolute) return Url.parseOrNull(this)
	if (uri.isRelative && context != null)
		return Uri.Builder()
			.scheme(context.scheme)
			.authority(context.authority)
			.path(uri.path)
			.fragment(uri.fragment)
			.build()
			.let { Url.parseOrNull(it.toString()) }
	return null
}

fun String.tryParseTrustedUrl(keepParams: Set<String>, disallowed: Set<String>, context: Url?) =
	tryParseUntrustedUrl(keepParams, context)
		?.let { if (it.isAbsolute) it else null }
		?.let { disallowed.forEach { path -> if ((it.path ?: "/").startsWith(path)) return null }}
		?.let { Url.parseOrNull(it.toString()) }

