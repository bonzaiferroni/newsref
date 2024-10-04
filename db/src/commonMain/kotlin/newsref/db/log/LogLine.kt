package newsref.db.log

class LogLine(
	val logWriter: LogWriter? = null
) {
	val builder = StringBuilder()
	var foreground = defaultForeground
	var background = defaultBackground
	var format = defaultText

	fun write(part: LogPart) = write(part.toString())

	fun write(part: String): LogLine {
		builder.append(part)
		logWriter?.write(part)
		return this
	}

	fun setForeground(hex: String) = setForeground(hex.toAnsiForeground())

	fun setForeground(foreground: Foreground): LogLine {
		if (this.foreground == foreground) return this
		write(foreground)
		this.foreground = foreground
		return this
	}

	fun setBackground(hex: String) = setBackground(hex.toAnsiBackground())

	fun setBackground(background: Background): LogLine {
		if (this.background == background) return this
		write(background)
		this.background = background
		return this
	}

	fun setFormat(format: Format): LogLine {
		if (this.format == format) return this
		write(format)
		this.format = format
		return this
	}

	fun cell(value: String, width: Int, justified: Justified): LogLine {
		write(value.fitToWidth(width, justified)).write(" ")
		return this
	}

	fun defaultFormat() = setFormat(defaultText)
	fun bold() = setFormat(boldText)
	fun underscore() = setFormat(underlineText)
	fun italicize() = setFormat(italicText)

	override fun toString() = builder.toString()

	fun clear(): LogLine {
		setForeground(defaultForeground)
		setBackground(defaultBackground)
		setFormat(defaultText)
		builder.clear()
		return this
	}
}

fun String.fitToWidth(width: Int, justified: Justified): String {
	return if (this.length >= width) {
		this.take(width) // Truncate if string is longer than `width`
	} else {
		this.pad(width, justified)
	}
}

fun String.pad(width: Int, justified: Justified) = when(justified) {
	Justified.LEFT -> this.padStart(width)
	Justified.RIGHT -> this.padEnd(width)
}