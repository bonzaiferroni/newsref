package newsref.krawly.clients

import java.io.File

fun promptTemplate(
    path: String,
    vararg items: Pair<String, String>
): String {
    val text = File(path).readText()
    val sb = StringBuilder(text)
    items.forEach { (key, value) ->
        val templateKey = "<|$key|>"
        var index = sb.indexOf(templateKey)
        if (index < 0) error("key not found in template: $key")
        sb.replace(index, index + templateKey.length, value)
    }
    return sb.toString()
}