package newsref.db.log

import kotlin.math.max

class LogTable(
	val name: String,
	val rowCount: Int,
	val live: Boolean,
	val color: Foreground,
	val console: LogConsole
) {
	val columns = mutableMapOf<String, LogColumn>()
	val rows = mutableListOf<String>()
	val builder = LogLine()
	val tableWidth get() = columns.map { it.value.width }.sum()

	fun log(name: String, value: String, minWidth: Int = 4, justified: Justified = Justified.RIGHT) {
		val column = columns[name] ?: LogColumn(name, minWidth, justified).also { columns[name] = it }
		val width = max(column.width, value.length)
		if (width > column.width) columns[name] = column.copy(width = width)
		builder.cell(value, width, column.justified)
		if (live) {
			val part = builder.toString()
			console.log(part, color)
			builder.clear()
		}
	}

	fun finalizeRow(): List<String>? {
		if (live) return null
		var line = builder.toString()
		builder.clear()
		rows.add(line)
		if (rows.size != rowCount) return null

		// add column names
		builder.clear().setForeground(color).underscore()
		for ((name, column) in columns) {
			builder.cell(name, column.width, column.justified)
		}
		line = builder.toString()
		rows.addFirst(line)

		// add table name
		line = builder.clear().setForeground(color)
			.write(name.padStart((tableWidth / 2) - (name.length / 2))).toString()
		rows.addFirst(line)
		val list = rows.toList()
		rows.clear()
		return list
	}
}

data class LogColumn(
	val name: String,
	val width: Int,
	val justified: Justified = Justified.RIGHT
)

enum class Justified {
	RIGHT,
	LEFT
}

typealias TableRow = MutableList<LogColumn>
typealias TableRows = MutableList<TableRow>
