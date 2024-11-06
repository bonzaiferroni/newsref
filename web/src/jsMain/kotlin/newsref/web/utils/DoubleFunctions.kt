package newsref.web.utils

import kotlin.math.pow


fun Double.format(decimalPlaces: Int = 1, showSign: Boolean = false) =
	"${if (showSign) "+" else ""}${this.roundToDecimalPlaces(decimalPlaces)}"

fun Double.roundToDecimalPlaces(decimalPlaces: Int): Double {
	val factor = 10.0.pow(decimalPlaces)
	return kotlin.math.round(this * factor) / factor
}