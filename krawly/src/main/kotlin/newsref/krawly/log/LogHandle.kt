package newsref.krawly.log

class LogHandle(
	val name: String,
	val console: LogConsole,
) {
	var status: String = ""
	var level: LogLevel = LogLevel.INFO

	fun log(message: String, level: LogLevel = LogLevel.INFO, status: String? = null) {
		if (status != null) this.status = status
		this.level = level
		console.log(name, level, message)
	}

	fun logTrace(message: String, status: String? = null) = log(message, LogLevel.TRACE, status)
	fun logDebug(message: String, status: String? = null) = log(message, LogLevel.DEBUG, status)
	fun logInfo(message: String, status: String? = null) = log(message, LogLevel.INFO, status)
	fun logWarning(message: String, status: String? = null) = log(message, LogLevel.WARNING, status)
	fun logError(message: String, status: String? = null) = log(message, LogLevel.ERROR, status)
}