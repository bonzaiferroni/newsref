package newsref.krawly.utils

fun String.wordCount() = this.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size

private val hostRegex = Regex("^(https?://[^/]+)")

fun String.getHostAndProtocol() = hostRegex.find(this)
fun String.hasHostAndProtocol() = hostRegex.matches(this)

private val pathRegex = Regex("https?://[^/]+(/.*)?")

fun String.getPath() = pathRegex.find(this)?.groups?.get(1)?.value ?: "/"
