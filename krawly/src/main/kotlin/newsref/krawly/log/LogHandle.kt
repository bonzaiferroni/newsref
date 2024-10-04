package newsref.krawly.log

class LogHandle(
	val name: String,
	val console: LogConsole,
) {
	var status: String = ""
	var level: LogLevel = LogLevel.INFO

	fun log(message: String, status: String? = null, level: LogLevel = LogLevel.INFO) {
		if (status != null) this.status = status
		this.level = level
		console.log(name, level, message)
	}

	fun logTrace(message: String, status: String? = null) = log(message, status, LogLevel.TRACE)
	fun logDebug(message: String, status: String? = null) = log(message, status, LogLevel.DEBUG)
	fun logInfo(message: String, status: String? = null) = log(message, status, LogLevel.INFO)
	fun logWarning(message: String, status: String? = null) = log(message, status, LogLevel.WARNING)
	fun logError(message: String, status: String? = null) = log(message, status, LogLevel.ERROR)
}