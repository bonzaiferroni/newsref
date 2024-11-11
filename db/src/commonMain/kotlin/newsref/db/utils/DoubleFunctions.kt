package newsref.db.utils

fun Double.format(decimalPlaces: Int = 1, showSign: Boolean = false) =
	"%${if (showSign) "+" else ""}.${decimalPlaces}f".format(this)

fun Float.format(decimalPlaces: Int = 1, showSign: Boolean = false) =
	"%${if (showSign) "+" else ""}.${decimalPlaces}f".format(this)