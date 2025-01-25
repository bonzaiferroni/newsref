package newsref.dashboard.utils

fun Int.twoDigits() = String.format("%02d", this)
fun Long.twoDigits() = String.format("%02d", this)
fun Float.twoDecimals() = String.format("%.2f", this)