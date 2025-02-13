package newsref.db.console

class Foreground(sequence: String): LogPart(sequence)
class Background(sequence: String): LogPart(sequence)
class Format(sequence: String): LogPart(sequence)
class Control(sequence: String): LogPart(sequence)

fun String.bold() = "$boldText$this$defaultText"
fun String.italic() = "$italicText$this$defaultText"
fun String.underline() = "$underlineText$this$defaultText"

abstract class LogPart(val sequence: String) {
	override fun toString() = sequence
	override fun equals(other: Any?) = other is LogPart && other.sequence == sequence
	override fun hashCode() = sequence.hashCode()
}

internal val moveCursorLeft = Control("\u001B[D")
internal val moveCursorRight = Control("\u001B[C")
internal val moveCursorUp = Control("\u001B[A")
internal val moveCursorDown = Control("\u001B[B")
internal val saveCursorPos = Control("\u001B[s")
internal val restoreCursorPos = Control("\u001B[u")
internal val clearLine = Control("\u001B[2K")
internal val clearScreen = Control("\u001B[2J")
internal val moveCursorToBeginningOfLine = Control("\u001B[1G")

internal val boldText = Format("\u001B[1m")
internal val italicText = Format("\u001B[3m")
internal val underlineText = Format("\u001B[4m")
internal val defaultText = Format("\u001B[0m")

fun eraseChars(count: Int) = Control("\u001B[${count}X")
fun moveCursorBackLines(count: Int) = Control("\u001B[${count}F")