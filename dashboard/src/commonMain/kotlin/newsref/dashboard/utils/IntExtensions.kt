package newsref.dashboard.utils

fun Int.twoDigits() = String.format("%02d", this)
fun Long.twoDigits() = String.format("%02d", this)
fun Float.formatDecimals() = String.format("%.2f", this)

fun Int.shortFormat(): String = when {
    this >= 1_000_000_000 -> "%.2fb".format(this / 1_000_000_000.0)
    this >= 1_000_000 -> "%.2fm".format(this / 1_000_000.0)
    this >= 1_000 -> "%.2fk".format(this / 1_000.0)
    else -> this.toString()
}