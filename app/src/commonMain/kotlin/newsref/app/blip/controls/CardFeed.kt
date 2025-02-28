package newsref.app.blip.controls

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.json.JsonNull.content
import newsref.app.blip.theme.*

@Composable
fun <Item> CardFeed(
    selectedId: Long?,
    items: ImmutableList<Item>,
    getId: (Item) -> Long,
    onSelect: ((Long) -> Unit)? = null,
    content: @Composable (Item, Boolean) -> Unit
) {
    val listState = rememberLazyListState()
    var selectedIndex by remember { mutableStateOf(0) }
    var autoScroll by remember { mutableStateOf(false)}

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

    fun select(index: Int) {
        if (index >= items.size) return
        selectedIndex = index
        val item = items[index]
        onSelect?.invoke(getId(item))
    }

    LaunchedEffect(selectedId) {
        if (selectedId != null && onSelect != null) {
            val index = items.indexOfFirst { getId(it) == selectedId }
            if (index < firstItemIndex || index > (lastItemIndex ?: 10)) {
                autoScroll = true
                listState.animateScrollToItem(index = index)
                autoScroll = false
            }

            select(index)
        }
    }

    LaunchedEffect(isPartiallyHidden, firstItemIndex) {
        if (!autoScroll && selectedIndex == firstItemIndex) {
            select(firstItemIndex + 1)
        }
    }

    LaunchedEffect(lastItemIndex) {
        if (!autoScroll && selectedIndex == lastItemIndex) {
            select(lastItemIndex!! - 1)
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

        fun getKey(index: Int, item: Item): Any = getId(item)

        LazyColumn(
            verticalArrangement = Blip.ruler.columnTight,
            state = listState,
        ) {
            itemsIndexed(items, ::getKey) { index, item ->
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