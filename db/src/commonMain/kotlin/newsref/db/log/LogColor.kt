package newsref.db.log

fun String.toColor(hex: String) = "${hex.toAnsiForeground()}$this$defaultForeground"
fun String.toBlue() = toColor(oceanBlue)
fun String.toOrange() = toColor(sunsetOrange)
fun String.toGreen() = toColor(emeraldGreen)
fun String.toPurple() = toColor(lavenderPurple)
fun String.toYellow() = toColor(goldenYellow)

fun String.toColorBg(hex: String) = "${hex.toAnsiBackground()}$this$defaultBackground"
fun String.toBlueBg() = toColorBg(deepspaceBlue)
fun String.toGreenBg() = toColorBg(midnightGreen)
fun String.toGrayBg() = toColorBg(charcoalGray)
fun String.toPurpleBg() = toColorBg(darkPlum)
fun String.toForestBg() = toColorBg(forestNight)

internal fun hexToRgb(hex: String): Triple<Int, Int, Int> {
	// Remove the '#' if it be there and convert to an integer
	val color = hex.removePrefix("#").toInt(16)
	// Extract the RGB components
	val red = (color shr 16) and 0xFF
	val green = (color shr 8) and 0xFF
	val blue = color and 0xFF
	// Return as a Triple
	return Triple(red, green, blue)
}

internal fun String.toAnsiForeground(): Foreground {
	val (r, g, b) = hexToRgb(this)
	return Foreground("\u001B[38;2;$r;$g;${b}m")
}

internal fun String.toAnsiBackground(): Background {
	val (r, g, b) = hexToRgb(this)
	return Background("\u001B[48;2;$r;$g;${b}m")
}

internal val escapeChar = Char(27).toString()
internal val defaultForeground = Foreground("\u001B[0m")
internal val defaultBackground = Background("\u001B[49m")
internal val blackForeground = Foreground("\u001B[30m")
internal val redForeground = Foreground("\u001B[31m")
internal val greenForeground = Foreground("\u001B[32m")
internal val yellowForeground = Foreground("\u001B[33m")
internal val blueForeground = Foreground("\u001B[34m")
internal val magentaForeground = Foreground("\u001B[35m")
internal val cyanForeground = Foreground("\u001B[36m")
internal val whiteForeground = Foreground("\u001B[37m")

internal val sourceColor = cyanForeground
internal val urgentColor = magentaForeground
internal val messageColor = defaultForeground

internal const val oceanBlue = "#3A8DE8"
internal const val sunsetOrange = "#FF4500"
internal const val emeraldGreen = "#50C878"
internal const val lavenderPurple = "#967BB6"
internal const val goldenYellow = "#FFD700"
internal val foregrounds = listOf(
	oceanBlue,
	sunsetOrange,
	emeraldGreen,
	lavenderPurple,
	goldenYellow
)
internal val oceanBlueFg = oceanBlue.toAnsiForeground()
internal val sunsetOrangeFg = sunsetOrange.toAnsiForeground()
internal val emeraldGreenFg = emeraldGreen.toAnsiForeground()
internal val lavenderPurpleFg = lavenderPurple.toAnsiForeground()
internal val goldenYellowFg = goldenYellow.toAnsiForeground()

internal const val deepspaceBlue = "#000033"
internal const val midnightGreen = "#004953"
internal const val charcoalGray = "#36454F"
internal const val darkPlum = "#4F2D7F"
internal const val forestNight = "#253529"
internal val backgrounds = listOf(
	deepspaceBlue,
	midnightGreen,
	charcoalGray,
	darkPlum,
	forestNight
)
