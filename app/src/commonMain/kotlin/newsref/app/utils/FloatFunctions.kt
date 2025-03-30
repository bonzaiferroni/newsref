package newsref.app.utils

import kotlin.math.pow
import kotlin.math.round

fun Float.format(decimals: Int = 2): String {
    val multiplier = 10.0.pow(decimals).toFloat()
    return (round(this * multiplier) / multiplier).toString()
}