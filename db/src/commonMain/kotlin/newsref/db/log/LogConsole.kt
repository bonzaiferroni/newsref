package newsref.db.log

import java.util.*

class LogConsole {
	var config = ConsoleConfig()
	var isActive = true
	private var reservedLines = 1
	private val builder = LineBuilder()
	private val partialBuilder = LineBuilder()
	private val handles = mutableListOf<LogHandle>()
	private var input = ""
	private var commands = mutableMapOf<String, (List<String>?) -> String>()
	private val queue = Collections.synchronizedList(mutableListOf<LogMessage>())
	private var lastSource = ""

	init {
		addCommand("quit") { isActive = false; "Goodbye!" }
	}

	fun addCommand(name: String, action: (List<String>?) -> String) {
		commands[name] = action
	}

	fun sendCommand(name: String, args: List<String>?): String {
		val command = commands[name] ?: return "Bad command or file name: $name"
		val result = command(args)
		log("console", LogLevel.INFO, result)
		return result
	}

	fun getHandle(name: String, showStatus: Boolean = false): LogHandle {
		if (showStatus) reservedLines++
		val handle = LogHandle(name, showStatus, this)
		handles.add(handle)
		return handle
	}

	fun log(message: String) = log("", LogLevel.INFO, message)

	fun log(source: String, level: LogLevel, message: String) {
		config.writer?.let { writer ->
			if (level.ordinal >= writer.minLevel.ordinal) writer.writeLine(source, level, message)
		}

		if (level.ordinal < config.minLevel.ordinal) return

		queue.add(LogMessage(source, message))
		while (queue.isNotEmpty()) {
			val (qSource, qMessage) = queue.removeFirstOrNull() ?: break
			val sourcePart = if (lastSource == source) "" else source
			val line = builder.bold().setForeground(level).writeLength(sourcePart, MAX_SOURCE_CHARS)
				.defaultFormat().defaultForeground().write(" ").write(qMessage).build()
			lastSource = source
			renderLog(qSource, line)
		}
	}

	fun logTrace(source: String, message: String) = log(source, LogLevel.TRACE, message)
	fun logDebug(source: String, message: String) = log(source, LogLevel.DEBUG, message)
	fun logInfo(source: String, message: String) = log(source, LogLevel.INFO, message)
	fun logWarning(source: String, message: String) = log(source, LogLevel.WARNING, message)
	fun logError(source: String, message: String) = log(source, LogLevel.ERROR, message)

	fun refreshLog() = renderLog(null, null)

	private fun renderLog(source: String?, newLine: String?) {
		if (config.showStatus) {
			repeat(reservedLines) {
				print(moveCursorBackLines(1))
				print(clearLine)
			}
		}

		if (newLine != null) {
			println(newLine)
		}

		if (config.showStatus) {
			for (handle in handles) {
				if (!handle.showStatus) continue
				// render partial lines
				print(clearLine)
				val partial = handle.partialLine.current()
				if (partial.isNotEmpty()) {
					val partialLine = partialBuilder.setForeground(dim).writeLength(handle.name, MAX_SOURCE_CHARS - 2)
						.defaultForeground().write(" > ").write(partial.takeLast(80)).build()
					println(partialLine) 										// reserved line
				} else {
					println()
				}
				// add to status bar
				builder.write("[").setForeground(handle.level)
				if (handle.name == source) builder.underscore()
				builder.write(handle.name).defaultFormat().defaultForeground()
				if (handle.status.isNotBlank()) builder.write(" ").write(handle.status)
				builder.write("]")
			}
			val statusLine = builder.build()
			println(statusLine) 												// reserved line
			print("> $input") 													// prompt
		}
	}

	fun addInput(char: Char) {
		print(moveCursorToBeginningOfLine)
		print(clearLine)
		if (char == '\n') {
			val array = input.split(' ')
			val command = array.firstOrNull()
			if (command != null) sendCommand(command, array.drop(1))
			input = ""
		} else if(char == '\b') {
			if (input.isNotEmpty()) {
				input = input.dropLast(1)
			}
		} else {
			input += char
		}
		print("> $input")
	}
}

const val MAX_SOURCE_CHARS = 8

private typealias LogMessage = Pair<String, String>

enum class LogLevel {
	TRACE,
	DEBUG,
	INFO,
	WARNING,
	ERROR,
}

data class ConsoleConfig(
	val showStatus: Boolean = false,
	val minLevel: LogLevel = LogLevel.DEBUG,
	val writer: LogWriter? = null
)

fun String.displayLength(): Int {
	var length = 0
	var inAnsiCode = false

	for (char in this) {
		if (char == '\u001B') {
			inAnsiCode = true // Start of an ANSI escape sequence
		} else if (inAnsiCode && char == 'm') {
			inAnsiCode = false // End of an ANSI escape sequence
		} else if (!inAnsiCode) {
			length++ // Count the character if not in an ANSI code
		}
	}

	return length
}