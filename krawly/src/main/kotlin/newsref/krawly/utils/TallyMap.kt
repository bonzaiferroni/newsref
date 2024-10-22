package newsref.krawly.utils

typealias TallyMap<T> = MutableMap<T, Int>

fun <T> TallyMap<T>.increment(key: T) {
	this[key] = this.getOrPut(key) { 0 } + 1
}

fun <T> TallyMap<T>.getCount(key: T) = this[key] ?: 0