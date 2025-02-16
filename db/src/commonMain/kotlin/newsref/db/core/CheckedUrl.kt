package newsref.db.core

import newsref.db.model.Host

class CheckedUrl internal constructor(
	rawUrl: String,
	junkParams: Set<String>?,
	bannedPaths: Set<String>?,
) : Url(rawUrl, junkParams, bannedPaths)

fun String.toCheckedUrl(
	junkParams: Set<String>,
	bannedPaths: Set<String>?
) = CheckedUrl(this, junkParams, bannedPaths)

fun String.toCheckedWithContext(
	context: Url,
	junkParams: Set<String>,
	bannedPaths: Set<String>?
) = CheckedUrl(this.maybeCombine(context), junkParams, bannedPaths)

fun String.toCheckedOrNull(
	junkParams: Set<String>,
	bannedPaths: Set<String>?
) = tryParseUrl { this.toCheckedUrl(junkParams, bannedPaths) }

fun String.toCheckedWithContextOrNull(
	context: Url,
	junkParams: Set<String>,
	bannedPaths: Set<String>?
) = tryParseUrl { this.toCheckedWithContext(context, junkParams, bannedPaths) }

fun String.toCheckedUrl(host: Host) = this.toCheckedUrl(host.junkParams, host.bannedPaths)

fun String.toCheckedWithContext(host: Host, context: Url) =
	this.toCheckedWithContext(context, host.junkParams, host.bannedPaths)

fun String.toCheckedOrNull(host: Host) = this.toCheckedOrNull(host.junkParams, host.bannedPaths)

fun String.toCheckedWithContextOrNull(host: Host, context: Url) =
	this.toCheckedWithContextOrNull(context, host.junkParams, host.bannedPaths)