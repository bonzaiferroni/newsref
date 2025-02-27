package newsref.app.blip.controls

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import kotlinx.collections.immutable.ImmutableList
import newsref.app.blip.theme.*

@Composable
fun <Item> CardFeed(
    items: ImmutableList<Item>,
    scrollId: Long? = null,
    getKey: ((Int, Item) -> Any)? = null,
    isSelected: ((Long, Item) -> Boolean)? = null,
    onFirstVisibleIndex: ((Int) -> Unit)? = null,
    content: @Composable (Item, Boolean) -> Unit
) {
    val listState = rememberLazyListState()
    var selectedIndex by remember { mutableStateOf(0) }

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

    val lastItemIndex by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }
    }

    LaunchedEffect(scrollId) {
        if (scrollId != null && isSelected != null) {
            val index = items.indexOfFirst { isSelected(scrollId, it) }
            listState.animateScrollToItem(index = index) // Scroll to item 10 smoothly
        }
    }

    LaunchedEffect(isPartiallyHidden, firstItemIndex) {
        if (selectedIndex == firstItemIndex) {
            selectedIndex = firstItemIndex + 1
        }
    }

    LaunchedEffect(lastItemIndex) {
        if (selectedIndex == lastItemIndex) {
            selectedIndex = lastItemIndex!! - 1
        }
    }

    Box {
        if (firstItemIndex < items.size) {
            val item = items[firstItemIndex]
            if (isPartiallyHidden) {
                val alpha =  (100 - listState.firstVisibleItemScrollOffset) / 100f
                Box(
                    modifier = Modifier.alpha(alpha)
                ) {
                    content(item, false)
                }
            }
        }

        LazyColumn(
            verticalArrangement = Blip.ruler.columnTight,
            state = listState,
        ) {
            itemsIndexed(items, getKey) { index, item ->
                val alpha = when {
                    index == firstItemIndex && isPartiallyHidden -> 0f
                    else -> 1f
                }
                Box(
                    modifier = Modifier.alpha(alpha)
                ) {
                    content(item, index == selectedIndex)
                }

            }
        }
    }

}