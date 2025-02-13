package newsref.db.console

interface LogWriter {
	val minLevel: LogLevel
	fun writeLine(source: String, level: LogLevel, line: String)
}

object PrintWriter: LogWriter {
	override val minLevel = LogLevel.TRACE
	override fun writeLine(source: String, level: LogLevel, line: String) {
		println(line)
	}
}