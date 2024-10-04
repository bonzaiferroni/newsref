package newsref.krawly.log


class LogConsole(
	val showStatus: Boolean = false,
	val minLevel: LogLevel = LogLevel.TRACE,
	val writer: LogWriter? = null
) {
	private val builder = LineBuilder()
	private val handles = mutableListOf<LogHandle>()

	fun getHandle(name: String): LogHandle {
		val handle = LogHandle(name, this)
		handles.add(handle)
		return handle
	}

	fun log(message: String) = log("", LogLevel.INFO, message)

	fun log(source: String, level: LogLevel, message: String) {
		if (writer != null) {
			if (level.ordinal >= writer.minLevel.ordinal) writer.writeLine(source, level, message)
		}

		if (level.ordinal < minLevel.ordinal) return
		if (showStatus) {
			print(moveCursorBackLines(1))
			print(clearLine)
		}

		val line = builder.bold().setForeground(level).writeLength(source, MAX_SOURCE_CHARS)
			.defaultFormat().defaultForeground().write(" ").write(message).build()
		println(line)

		if (showStatus) {
			for (handle in handles) {
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
