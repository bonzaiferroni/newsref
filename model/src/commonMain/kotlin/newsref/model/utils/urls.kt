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

fun String.removeQueryParameters() = this.split('?')[0]