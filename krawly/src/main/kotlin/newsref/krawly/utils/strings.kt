package newsref.krawly.utils

fun String.wordCount() = this.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size

private val hostRegex = Regex("^(https?://[^/]+)")

fun String.getHostAndProtocol(): String? = hostRegex.find(this)?.groupValues?.get(1)
fun String.hasHostAndProtocol(): Boolean = hostRegex.find(this) != null

private val pathRegex = Regex("https?://[^/]+(/.*)?")

fun String.getPath() = pathRegex.find(this)?.groups?.get(1)?.value ?: "/"

