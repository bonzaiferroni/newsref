package newsref.db.log

class LogConsole {
	var config = ConsoleConfig()
	var isActive = true
	private val builder = LineBuilder()
	private val handles = mutableListOf<LogHandle>()
	private var input = ""
	private var commands = mutableMapOf<String, (List<String>?) -> String>()

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
		if (config.showStatus) {
			print(moveCursorBackLines(1))
			print(clearLine)
		}

		val line = builder.bold().setForeground(level).writeLength(source, MAX_SOURCE_CHARS)
			.defaultFormat().defaultForeground().write(" ").write(message).build()
		println(line)

		if (config.showStatus) {
			for (handle in handles) {
				if (!handle.showStatus) continue
				builder.write("[").setForeground(handle.level)
				if (handle.name == source) builder.underscore()
				builder.write(handle.name).defaultFormat().defaultForeground()
				if (handle.status.isNotBlank()) builder.write(" ").write(handle.status)
				builder.write("]")
			}
			val statusLine = builder.build()
			println(statusLine)
			print("> $input")
		}
	}

	fun addInput(char: Char) {
		print(moveCursorToBeginningOfLine)
		print(clearLine)
		if (char == '\n') {
			if (input == "quit") isActive = false
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

	fun logTrace(source: String, message: String) = log(source, LogLevel.TRACE, message)
	fun logDebug(source: String, message: String) = log(source, LogLevel.DEBUG, message)
	fun logInfo(source: String, message: String) = log(source, LogLevel.INFO, message)
	fun logWarning(source: String, message: String) = log(source, LogLevel.WARNING, message)
	fun logError(source: String, message: String) = log(source, LogLevel.ERROR, message)
}

const val MAX_SOURCE_CHARS = 10

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
