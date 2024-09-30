package newsref.krawly.utils

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
    return disallowedPaths
}

fun List<String>.isPathAllowed(path: String): Boolean {
    return this.none { disallowedPath -> path.startsWith(disallowedPath) }
}

fun String.getRobotsTxtUrl() = this.getHostAndProtocol()?.value?.let { "$it/robots.txt"}
fun String.getSitemapUrl() = this.getHostAndProtocol()?.value?.let { "$it/sitemap.xml"}