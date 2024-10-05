package newsref.db.log

class LogConsole {
	var config = ConsoleConfig()
	private val builder = LineBuilder()
	private val handles = mutableListOf<LogHandle>()

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
		}
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