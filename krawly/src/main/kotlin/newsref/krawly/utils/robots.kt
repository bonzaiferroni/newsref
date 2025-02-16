package newsref.krawly.utils

import newsref.db.core.Url
import newsref.db.core.toUrlWithContextOrNull

fun parseRobotsTxt(content: String): Set<String> {
    val disallowedPaths = mutableSetOf<String>()
    val lines = content.lines()

    var isGlobalAgent = false
    for (line in lines) {
        // Remove comments and trim whitespace
        val cleanedLine = line.substringBefore("#").trim()

        when {
            cleanedLine.startsWith("User-agent:", ignoreCase = true) -> {
                // Check if it's for global user-agent or reset flag if a new user-agent block starts
                isGlobalAgent = cleanedLine.substringAfter("User-agent:", "").trim() == "*"
            }
            isGlobalAgent && cleanedLine.startsWith("Disallow:", ignoreCase = true) -> {
                // If in a global block, capture Disallow paths
                disallowedPaths += cleanedLine.substringAfter("Disallow:", "").trim()
            }
            cleanedLine.isBlank() -> {
                // Reset on empty line, marking the end of a block
                isGlobalAgent = false
            }
        }
    }
    return disallowedPaths.filter { it.isNotBlank() }.toSet()
}

fun Url.getRobotsTxtUrl() = "/robots.txt".toUrlWithContextOrNull(this)
    ?: throw IllegalArgumentException("Invalid robots.txt URL: $this")
fun Url.getSitemapUrl() = "/sitemap.xml".toUrlWithContextOrNull(this)
    ?: throw IllegalArgumentException("Invalid sitemap.xml URL: $this")