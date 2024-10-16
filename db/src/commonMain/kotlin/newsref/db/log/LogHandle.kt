package newsref.db.log

class LogHandle(
	val name: String,
	val showStatus: Boolean,
	private val console: LogConsole,
) {
	var status: String = ""
	var level: LogLevel = LogLevel.INFO
	var partialLine = LineBuilder()

	fun log(message: String, status: Any? = null, level: LogLevel = LogLevel.INFO) {
		if (status != null) this.status = status.toString()
		this.level = level
		console.log(name, level, message)
	}

	fun logPartial(message: String, refresh: Boolean = false) {
		if (partialLine.isEmpty) partialLine.write(message)
		else partialLine.setForeground(dim).write(" | ").defaultForeground().write(message)
		if (refresh) console.refreshLog()
	}

	fun cell(
		emoji: String,
		width: Int? = null,
		justify: Justify = Justify.RIGHT,
		block: (() -> Boolean)? = null
	): LogHandle {
		val partialMsg = if (block == null || block()) emoji else "💢"
		val paddedMsg = width?.let {
			if (justify == Justify.RIGHT) {
				partialMsg.padStart(it).takeLast(it)
			} else {
				partialMsg.padEnd(it).take(it)
			}
		} ?: partialMsg
		logPartial(paddedMsg)
		return this
	}

	fun row(message: String = "", level: LogLevel = LogLevel.INFO) {
		logPartial(message)
		val line = partialLine.build()
		console.log(name, level, line)
	}

	fun logTrace(message: String, status: Any? = null) = log(message, status, LogLevel.TRACE)
	fun logDebug(message: String, status: Any? = null) = log(message, status, LogLevel.DEBUG)
	fun logInfo(message: String, status: Any? = null) = log(message, status, LogLevel.INFO)
	fun logWarning(message: String, status: Any? = null) = log(message, status, LogLevel.WARNING)
	fun logError(message: String, status: Any? = null) = log(message, status, LogLevel.ERROR)
}

enum class Justify {LEFT, RIGHT}