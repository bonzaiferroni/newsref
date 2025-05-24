package newsref.db.console

import kabinet.log.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.reflect.KClass

class LogConsole {
	var config = ConsoleConfig()
	var isActive = true
	private val statusBuilder = LineBuilder()
	private val builder = LineBuilder()
	private val handles = Collections.synchronizedList(mutableListOf<LogHandle>())
	private var input = ""
	private var commands = mutableMapOf<String, (List<String>?) -> String>()
	private val queue = Collections.synchronizedList(mutableListOf<LogMessage>())
	private var lastSource = ""

	init {
		addCommand("quit") { isActive = false; "Goodbye!" }
		startQueueConsumer()
	}

	fun addCommand(name: String, action: (List<String>?) -> String) {
		commands[name] = action
	}

	fun sendCommand(name: String, args: List<String>?): String {
		val command = commands[name] ?: return "Bad command or file name: $name"
		val result = command(args)
		log("console", LogLevel.INFO, result)
		return result
	}

	fun getHandle(name: String, showStatus: Boolean = false): LogHandle {
		val handle = LogHandle(name, showStatus, this)
		handles.add(handle)
		return handle
	}

	fun <T: Any> getHandle(type: KClass<T>, showStatus: Boolean = false) =
		getHandle(type.simpleName ?: error("Must be named type"), showStatus)

	fun log(message: String) = log("", LogLevel.INFO, message)

	fun log(source: String, level: LogLevel, message: Any?) {
		val msg = message.toString()
		config.writer?.let { writer ->
			if (level.ordinal >= writer.minLevel.ordinal) writer.writeLine(source, level, msg)
		}

		if (level.ordinal < config.minLevel.ordinal) return

		queue.add(LogMessage(level, source, msg))
	}

	fun logTrace(source: String, message: String) = log(source, LogLevel.TRACE, message)
	fun logDebug(source: String, message: String) = log(source, LogLevel.DEBUG, message)
	fun logInfo(source: String, message: String) = log(source, LogLevel.INFO, message)
	fun logWarning(source: String, message: String) = log(source, LogLevel.WARNING, message)
	fun logError(source: String, message: String) = log(source, LogLevel.ERROR, message)

	fun refreshLog() = renderLog(null, null)

	private fun startQueueConsumer() {
		CoroutineScope(Dispatchers.Default).launch {
			while (true) {
				while (queue.isNotEmpty()) {
					val (level, source, message) = queue.removeFirstOrNull() ?: break
					val sourcePart = if (lastSource == source) "" else source
					val line = builder.bold().setForeground(level).writeLength(sourcePart, MAX_SOURCE_CHARS)
						.defaultFormat().defaultForeground().write(" ").write(message).build()
					lastSource = source
					renderLog(source, line)
					if (queue.size < 20)
						delay(50)
				}
				delay(10) // don't spin yer gears
			}
		}
	}

	private fun renderLog(source: String?, newLine: String?) {
		if (config.showStatus) {
			print(moveCursorToBeginningOfLine)
			print(clearLine)
		}

		if (newLine != null) {
			println(newLine)
		}

		if (config.showStatus) {
			synchronized(handles) {
				for (handle in handles) {
					if (!handle.showStatus) continue
					// add to status bar
					statusBuilder.write("[").setForeground(handle.level)
					if (handle.name == source) statusBuilder.underscore()
					statusBuilder.write(handle.name).defaultFormat().defaultForeground()
					if (handle.status.isNotBlank()) statusBuilder.write(" ").write(handle.status)
					statusBuilder.write("]")
				}
				val statusLine = statusBuilder.build()
				print(statusLine)
			}
		}
	}

	fun addInput(char: Char) {
		if (char == '\n') {
			val array = input.split(' ')
			val command = array.firstOrNull()
			if (command != null) sendCommand(command, array.drop(1))
			input = ""
		} else if (char == '\b') {
			if (input.isNotEmpty()) {
				input = input.dropLast(1)
			}
		} else {
			input += char
		}
	}
}

const val MAX_SOURCE_CHARS = 8

private typealias LogMessage = Triple<LogLevel, String, String>

data class ConsoleConfig(
	val showStatus: Boolean = false,
	val minLevel: LogLevel = LogLevel.DEBUG,
	val writer: LogWriter? = null
)

fun String.displayLength(): Int {
	var length = 0
	var inAnsiCode = false

	for (char in this) {
		if (char == '\u001B') {
			inAnsiCode = true // Start of an ANSI escape sequence
		} else if (inAnsiCode && char == 'm') {
			inAnsiCode = false // End of an ANSI escape sequence
		} else if (!inAnsiCode) {
			length++ // Count the character if not in an ANSI code
		}
	}

	return length
}