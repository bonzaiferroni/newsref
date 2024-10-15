package newsref.model.core

import newsref.model.data.Host

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

fun String.toCheckedUrl(host: Host) = this.toCheckedUrl(host.junkParams, host.bannedPaths)

fun String.toCheckedWithContext(host: Host, context: Url) =
	this.toCheckedWithContext(context, host.junkParams, host.bannedPaths)

fun String.toCheckedOrNull(host: Host) = this.toCheckedOrNull(host.junkParams, host.bannedPaths)

fun String.toCheckedWithContextOrNull(host: Host, context: Url) =
	this.toCheckedWithContextOrNull(context, host.junkParams, host.bannedPaths)