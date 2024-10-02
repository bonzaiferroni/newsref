package newsref.model.core

class CheckedUrl(
	rawUrl: String,
	requiredParams: Set<String>?,
	disallowedPaths: Set<String>?,
) : Url(rawUrl, requiredParams, disallowedPaths) {

	companion object {
		fun parse(
			url: String,
			requiredParams: Set<String>,
			disallowedPaths: Set<String>
		) = CheckedUrl(url, requiredParams, disallowedPaths)

		fun parseMaybeRelative(
			url: String,
			context: Url,
			requiredParams: Set<String>,
			disallowedPaths: Set<String>
		) = CheckedUrl(url.maybeCombine(context), requiredParams, disallowedPaths)

		fun tryParse(
			url: String,
			requiredParams: Set<String>,
			disallowedPaths: Set<String>
		) = tryParseUrl { parse(url, requiredParams, disallowedPaths) }

		fun tryParseMaybeRelative(
			url: String,
			context: Url,
			requiredParams: Set<String>,
			disallowedPaths: Set<String>
		) = tryParseUrl { parseMaybeRelative(url, context, requiredParams, disallowedPaths) }
	}
}