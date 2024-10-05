package newsref.db.log

class LogHandle(
	val name: String,
	val console: LogConsole,
) {
	var status: String = ""
	var level: LogLevel = LogLevel.INFO

	fun log(message: String, status: Any? = null, level: LogLevel = LogLevel.INFO) {
		if (status != null) this.status = status.toString()
		this.level = level
		console.log(name, level, message)
	}

	fun logTrace(message: String, status: Any? = null) = log(message, status, LogLevel.TRACE)
	fun logDebug(message: String, status: Any? = null) = log(message, status, LogLevel.DEBUG)
	fun logInfo(message: String, status: Any? = null) = log(message, status, LogLevel.INFO)
	fun logWarning(message: String, status: Any? = null) = log(message, status, LogLevel.WARNING)
	fun logError(message: String, status: Any? = null) = log(message, status, LogLevel.ERROR)
}