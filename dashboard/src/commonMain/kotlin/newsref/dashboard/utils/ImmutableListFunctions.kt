package newsref.dashboard.utils

import kotlinx.collections.immutable.*

fun <T> emptyImmutableList() = emptyList<T>().toImmutableList()
fun <K, V> emptyImmutableMap() = emptyMap<K, V>().toImmutableMap()
fun <T> immutableListOf(vararg elements: T) = elements.asList().toImmutableList()