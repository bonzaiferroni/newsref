package newsref.db.log

interface LogWriter {
	fun writeLine(line: String = "")
	fun write(msg: String)
	fun write(part: LogPart)
}

object PrintWriter: LogWriter {
	override fun writeLine(line: String) = println(line)
	override fun write(msg: String) = print(msg)
	override fun write(part: LogPart) = write(part.toString())
}