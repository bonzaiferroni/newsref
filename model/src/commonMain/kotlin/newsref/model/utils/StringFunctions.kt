package newsref.model.utils

fun String.obfuscate(): String {
    return this.map { it.code.xor('s'.code).toChar() }.joinToString("")
}

fun String.deobfuscate(): String {
    return this.map { it.code.xor('s'.code).toChar() }.joinToString("")
}

fun String.takeEllipsis(length: Int, ellipsis: String = "..."): String =
    if (this.length > length) take(length) + ellipsis else this
