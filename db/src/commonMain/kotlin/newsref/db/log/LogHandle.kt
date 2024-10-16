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

	fun logIfTrue(emoji: String, width: Int? = null, block: (() -> Boolean)? = null) {
		val partialMsg = if (block == null || block()) emoji else "💢"
		val paddedMsg = width?.let { partialMsg.padStart(it).take(it) } ?: partialMsg
		logPartial(paddedMsg)
	}

	fun finishPartial(message: String = "", level: LogLevel = LogLevel.INFO) {
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