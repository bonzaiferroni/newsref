package newsref.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.collections.immutable.toImmutableList

@Composable
fun <T> List<T>.rememberImmutableList() = remember { this.toImmutableList() }