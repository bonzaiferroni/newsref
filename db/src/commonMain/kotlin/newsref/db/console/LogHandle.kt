package newsref.db.console

class LogHandle(
	val name: String,
	val showStatus: Boolean,
	private val console: LogConsole,
) {
	var status: String = ""
	var level: LogLevel = LogLevel.INFO
	var partialLine = LineBuilder()

	fun log(message: Any?, status: Any? = null, level: LogLevel = LogLevel.INFO) {
		if (status != null) this.status = status.toString()
		this.level = level
		console.log(name, level, message)
	}

	fun logPartial(message: String, refresh: Boolean = false) {
		if (partialLine.isEmpty) partialLine.write(message)
		else partialLine.setForeground(dark).write("┃").defaultForeground().write(message)
		if (refresh) console.refreshLog()
	}

	fun cell(
		value: Any,
		width: Int? = null,
		label: String? = null,
		justify: Justify = Justify.RIGHT,
		highlight: Boolean = false,
		isValid: (() -> Boolean?)? = null
	): LogHandle {
		val showValue = isValid == null || isValid() == true
		var content = if (showValue) value.toString() else "💢"
		content = width?.let {
			val labelWidth = label?.let { minOf(width - content.length - 1, label.length) } ?: 0
			val contentWidth = labelWidth.takeIf{ labelWidth > 0 }?.let { width - labelWidth - 1 } ?: width
			val contentPart = if (justify == Justify.RIGHT) {
				content.padStart(contentWidth).takeLast(contentWidth)
			} else {
				content.padEnd(contentWidth).take(contentWidth)
			}.let { if (highlight) it.toGreen() else it }
			if (labelWidth > 0 && label != null) {
				val labelPart = label.take(labelWidth)
				if (justify == Justify.RIGHT) {
					"${labelPart.dark()} $contentPart"
				} else {
					"$contentPart ${labelPart.dark()}"
				}
			} else {
				contentPart
			}
		} ?: content
		logPartial(content)
		return this
	}

	fun send(
		message: String? = null,
		level: LogLevel = LogLevel.INFO,
		background: Background? = null,
		width: Int? = null,
	) {
		message?.let { logPartial(it) }
		var line = partialLine.build().let { line ->
			if (width == null) return@let line
			val displayLength = line.displayLength()
			if (displayLength >= width) return@let line
			return@let "$line${"".padEnd(width - displayLength, ' ')}"
		}
		background?.let{ line = line.toColorBg(it) }
		console.log(name, level, line)
	}

	fun logTrace(message: String, status: Any? = null) = log(message, status, LogLevel.TRACE)
	fun logDebug(message: String, status: Any? = null) = log(message, status, LogLevel.DEBUG)
	fun logInfo(message: String, status: Any? = null) = log(message, status, LogLevel.INFO)
	fun logWarning(message: String, status: Any? = null) = log(message, status, LogLevel.WARNING)
	fun logError(message: String, status: Any? = null) = log(message, status, LogLevel.ERROR)
}

enum class Justify {LEFT, RIGHT}