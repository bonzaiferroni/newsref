package newsref.app.blip.controls

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import kotlinx.collections.immutable.ImmutableList
import newsref.app.blip.theme.*

@Composable
fun <T> DataFeed(
    items: ImmutableList<T>,
    content: @Composable (T) -> Unit
) {
    val listState = rememberLazyListState()

    val isPartiallyHidden by remember {
        derivedStateOf {
            listState.firstVisibleItemScrollOffset > 0
        }
    }

    val firstItemIndex by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex
        }
    }

    Box {
        LazyColumn(
            verticalArrangement = Blip.ruler.columnTight,
            state = listState,
        ) {
            itemsIndexed(items) { index, item ->
                val alpha = when {
                    index == firstItemIndex && isPartiallyHidden -> (100 - listState.firstVisibleItemScrollOffset) / 100f
                    else -> 1f
                }
                Box(
                    modifier = Modifier.alpha(alpha)
                ) {
                    content(item)
                }

            }
        }
    }

}