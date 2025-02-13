package newsref.model.utils

inline fun <T> Iterable<T>.sumOfFloat(
    selector: (T) -> Float
): Float = this.sumOf { selector(it).toDouble() }.toFloat()