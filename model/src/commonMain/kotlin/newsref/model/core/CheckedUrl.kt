package newsref.model.core

class CheckedUrl internal constructor(
	rawUrl: String,
	requiredParams: Set<String>?,
	disallowedPaths: Set<String>?,
) : Url(rawUrl, requiredParams, disallowedPaths)

fun String.parseChecked(
	requiredParams: Set<String>,
	disallowedPaths: Set<String>
) = CheckedUrl(this, requiredParams, disallowedPaths)

fun String.parseCheckedMaybeRelative(
	context: Url,
	requiredParams: Set<String>,
	disallowedPaths: Set<String>
) = CheckedUrl(this.maybeCombine(context), requiredParams, disallowedPaths)

fun String.parseCheckedOrNull(
	requiredParams: Set<String>,
	disallowedPaths: Set<String>
) = tryParseUrl { this.parseChecked(requiredParams, disallowedPaths) }

fun String.parseCheckedMaybeRelativeOrNull(
	context: Url,
	requiredParams: Set<String>,
	disallowedPaths: Set<String>
) = tryParseUrl { this.parseCheckedMaybeRelative(context, requiredParams, disallowedPaths) }