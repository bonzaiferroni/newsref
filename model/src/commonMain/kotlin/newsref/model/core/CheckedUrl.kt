package newsref.model.core

import newsref.model.data.Outlet

class CheckedUrl internal constructor(
	rawUrl: String,
	junkParams: Set<String>?,
	disallowedPaths: Set<String>?,
) : Url(rawUrl, junkParams, disallowedPaths)

fun String.toCheckedUrl(
	junkParams: Set<String>,
	disallowedPaths: Set<String>?
) = CheckedUrl(this, junkParams, disallowedPaths)

fun String.toCheckedWithContext(
	context: Url,
	junkParams: Set<String>,
	disallowedPaths: Set<String>?
) = CheckedUrl(this.maybeCombine(context), junkParams, disallowedPaths)

fun String.toCheckedOrNull(
	junkParams: Set<String>,
	disallowedPaths: Set<String>?
) = tryParseUrl { this.toCheckedUrl(junkParams, disallowedPaths) }

fun String.toCheckedWithContextOrNull(
	context: Url,
	junkParams: Set<String>,
	disallowedPaths: Set<String>?
) = tryParseUrl { this.toCheckedWithContext(context, junkParams, disallowedPaths) }

fun String.toCheckedUrl(outlet: Outlet) = this.toCheckedUrl(outlet.junkParams, outlet.disallowed)

fun String.toCheckedWithContext(outlet: Outlet, context: Url) =
	this.toCheckedWithContext(context, outlet.junkParams, outlet.disallowed)

fun String.toCheckedOrNull(outlet: Outlet) = this.toCheckedOrNull(outlet.junkParams, outlet.disallowed)

fun String.toCheckedWithContextOrNull(outlet: Outlet, context: Url) =
	this.toCheckedWithContextOrNull(context, outlet.junkParams, outlet.disallowed)