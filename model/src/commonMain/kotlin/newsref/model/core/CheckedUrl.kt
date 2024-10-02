package newsref.model.core

import newsref.model.data.Outlet

class CheckedUrl internal constructor(
	rawUrl: String,
	requiredParams: Set<String>?,
	disallowedPaths: Set<String>?,
) : Url(rawUrl, requiredParams, disallowedPaths)

fun String.toCheckedUrl(
	requiredParams: Set<String>,
	disallowedPaths: Set<String>?
) = CheckedUrl(this, requiredParams, disallowedPaths)

fun String.toCheckedWithContext(
	context: Url,
	requiredParams: Set<String>,
	disallowedPaths: Set<String>?
) = CheckedUrl(this.maybeCombine(context), requiredParams, disallowedPaths)

fun String.toCheckedOrNull(
	requiredParams: Set<String>,
	disallowedPaths: Set<String>?
) = tryParseUrl { this.toCheckedUrl(requiredParams, disallowedPaths) }

fun String.toCheckedWithContextOrNull(
	context: Url,
	requiredParams: Set<String>,
	disallowedPaths: Set<String>?
) = tryParseUrl { this.toCheckedWithContext(context, requiredParams, disallowedPaths) }

fun String.toCheckedUrl(outlet: Outlet) = this.toCheckedUrl(outlet.urlParams, outlet.disallowed)

fun String.toCheckedWithContext(outlet: Outlet, context: Url) =
	this.toCheckedWithContext(context, outlet.urlParams, outlet.disallowed)

fun String.toCheckedOrNull(outlet: Outlet) = this.toCheckedOrNull(outlet.urlParams, outlet.disallowed)

fun String.toCheckedWithContextOrNull(outlet: Outlet, context: Url) =
	this.toCheckedWithContextOrNull(context, outlet.urlParams, outlet.disallowed)