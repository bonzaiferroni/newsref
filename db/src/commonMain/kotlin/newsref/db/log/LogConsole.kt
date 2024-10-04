package newsref.db.log


class LogConsole(
	val writer: LogWriter = PrintWriter
) {
	private val tables = mutableMapOf<String, LogTable>()
	private val sourceLines = mutableMapOf<String, MutableList<String>>()
	private var indexer = 0
	private val line = LogLine(writer)

	fun getTable(
		name: String,
		rowCount: Int,
		live: Boolean,
		color: String = foregrounds.getByIndexer(indexer++)
	) = tables[name] ?: LogTable(name, rowCount, live, color.toAnsiForeground(), this).also { tables[name] = it }

	fun log(message: String, foreground: Foreground = defaultForeground) {
		line.setForeground(foreground)
		line.write(message)
		line.setForeground(defaultForeground)
	}

	fun logLine(source: String, message: String, foreground: Foreground = defaultForeground, urgent: Boolean = false) {
		val sourcePart = source.take(MAX_SOURCE_CHARS).padEnd(MAX_SOURCE_CHARS)
		val line = "$foreground$message$defaultForeground"
		if (urgent) {
			val prefix = "$boldText$urgentColor$sourcePart$defaultText  "
			writer.write(clearLine)
			writer.writeLine(prefix + line)
			writer.write(this.line.toString())
		} else {
			val lines = sourceLines[source] ?: mutableListOf<String>().also { sourceLines[source] = it }
			lines.add(line)
		}
	}

	fun finalizeLoop() {
		for (sourceLine in sourceLines) {
			val source = sourceLine.key
			val lines = sourceLine.value
			val sourcePart = source.take(MAX_SOURCE_CHARS).padEnd(MAX_SOURCE_CHARS)
			val prefix = "$boldText$sourceColor$sourcePart$defaultText  "

			writer.write(prefix)
			for (line in lines) {
				writer.writeLine(line)
				writer.write(blankPrefix)
			}
			writer.writeLine()
		}

		for (kvp in tables) {
			val table = kvp.value
			val rows = table.finalizeRow()
			if (rows == null) continue
			for (row in rows) {
				writer.writeLine(row)
			}
		}
	}
}

const val MAX_SOURCE_CHARS = 10
val blankPrefix = "${" ".repeat(MAX_SOURCE_CHARS)}  "

fun <T> List<T>.getByIndexer(indexer: Int) = this[indexer % this.size]


