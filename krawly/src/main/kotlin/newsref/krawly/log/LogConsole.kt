package newsref.krawly.log


class LogConsole(
	val writer: LogWriter = PrintWriter,
	val showStatus: Boolean = false
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

enum class LogLevel(level: Int) {
	TRACE(0),
	DEBUG(1),
	INFO(2),
	WARNING(3),
	ERROR(4),
}
