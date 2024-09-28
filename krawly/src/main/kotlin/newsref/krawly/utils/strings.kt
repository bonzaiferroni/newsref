package newsref.krawly.utils

fun String.wordCount() = this.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
