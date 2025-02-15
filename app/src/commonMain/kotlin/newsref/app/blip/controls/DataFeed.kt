package newsref.app.blip.controls

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.*
import kotlinx.collections.immutable.ImmutableList
import newsref.app.blip.theme.*

@Composable
fun <T> DataFeed(items: ImmutableList<T>, content: @Composable (T) -> Unit) {
    LazyColumn {
        items(items) {
            content(it)
        }
    }
}