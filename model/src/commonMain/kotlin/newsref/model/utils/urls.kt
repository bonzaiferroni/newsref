package newsref.model.utils

fun String.getApexDomain() = this
    .removePrefix("http://")
    .removePrefix("https://")
    .split('/')[0]
    .split('.')
    .apply {
        if (size < 2) {
            throw IllegalArgumentException("Invalid URL: Could not determine apex domain: ${this@getApexDomain}")
        }
    }
    .takeLast(2)
    .joinToString(".")

fun String.removeQueryParameters(keepParams: List<String>): String {
    val parts = this.split('?', limit = 2)
    if (parts.size == 1) return this // No query params to strip

    val baseUrl = parts[0]
    val queryParams = parts[1]
        .split('&')
        .mapNotNull { param -> param.split('=', limit = 2).takeIf { it[0] in keepParams }?.joinToString("=") }
        .joinToString("&")

    return if (queryParams.isEmpty()) baseUrl else "$baseUrl?$queryParams"
}
