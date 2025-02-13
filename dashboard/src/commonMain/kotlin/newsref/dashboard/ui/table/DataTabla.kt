package newsref.dashboard.ui.table

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import newsref.dashboard.*
import newsref.dashboard.ui.theme.*
import newsref.dashboard.utils.*

@Composable
fun <T> DataTabla(
    items: List<T>,
    block: TableConfig<T>.() -> Unit
) {
    val config = remember {
        val config = TableConfig<T>()
        block(config)
        config
    }

    // Create a LazyListState
    val listState = rememberLazyListState()

    // Observe scroll position using derived state
    val firstVisibleIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex }
    }

    LaunchedEffect(firstVisibleIndex) {
        config.onFirstVisibleIndex?.let { it(firstVisibleIndex) }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(halfSpacing),
        state = listState,
    ) {
        config.name?.let {
            item {
                Text(text = it, style = MaterialTheme.typography.headlineSmall)
            }
        }

        item {
            val bgColor = config.color.darken(.5f)
            Column(
                modifier = Modifier
                    .border(2.dp, Color.White.copy(alpha = 0.2f), roundedHeader)
                    .clip(roundedHeader)
                    .background(bgColor)
                    .padding(innerPadding)
            ) {
                for (columnSet in config.columnSets) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (column in columnSet) {
                            Box(
                                contentAlignment = column.align.toAlignment(),
                                modifier = Modifier.alpha(column.alpha)
                                    .clip(RoundedCornerShape(6.dp))
                                    .modifyIfNotNull(column.width) { this.width(it.dp) }
                                    .modifyIfNotNull(column.weight) { this.weight(it) }
                                    .padding(innerPadding)
                            ) {
                                TextCell(column.name)
                            }
                        }
                    }
                }
            }
        }
        items(items, config.getKey) { item ->
            val bgColor =  config.glowFunction?.let { function ->
                function(item)?.let { config.color.scaleBrightness(it * .5f) }
            } ?: config.color
            val isNew = config.isNew
            val initialValue = if (isNew != null && isNew(item)) 0f else 1f
            var alpha = remember { Animatable(initialValue) } // Start fully transparent

            LaunchedEffect(item) {
                if (isNew != null && isNew(item)) alpha.snapTo(0f)
                alpha.animateTo(1f, animationSpec = tween(500, delayMillis = 200, easing = EaseOut))
            }

            Column(
                modifier = Modifier.modifyIfNotNull(config.onClickRow) { this.clickable { it(item) } }
                    .animateItem()
                    .alpha(alpha.value)
                    .offset(x = ((1 - alpha.value) * 10).dp)
                    .border(2.dp, Color.White.copy(alpha = 0.2f), roundedCorners)
                    .clip(roundedCorners)
                    .background(bgColor)
                    .padding(innerPadding)
            ) {
                for (columnSet in config.columnSets) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (column in columnSet) {
                            Row(
                                modifier = Modifier
                                    .modifyIfNotNull(column.width) { this.width(it.dp) }
                                    .modifyIfNotNull(column.weight) { this.weight(it) }
                                    .background(color = config.color)
                            ) {
                                Box(
                                    contentAlignment = column.align.toAlignment(),
                                    modifier = Modifier.weight(1f)
                                        .alpha(column.alpha)
                                        .clip(RoundedCornerShape(6.dp))
                                        .modifyIfNotNull(column.onClickCell) { this.clickable(onClick = { it(item)}) }
                                        .padding(innerPadding)
                                ) {
                                    column.content(item)
                                }
                                CellControlRow(item, column.controls)
                            }
                        }
                    }
                }
            }
        }
    }
}

class TableConfig<T> {
    private val _columnSets = mutableListOf<List<TableColumn<T>>>()
    val columnSets: List<List<TableColumn<T>>> get () = _columnSets

    var name: String? = null
    var color: Color = primaryContainerDark.darken(.5f)
    var getKey: ((T) -> Any)? = null
    var isNew: ((T) -> Boolean)? = null
    var glowFunction: ((T) -> Float?)? = null
    var onFirstVisibleIndex: ((Int) -> Unit)? = null
    var onClickRow: ((T) -> Unit)? = null

    fun addColumnSet(vararg columns: TableColumn<T>) {
        _columnSets.add(columns.asList())
    }
}

fun AlignCell?.toAlignment() = when (this) {
    AlignCell.Left, null -> Alignment.TopStart
    AlignCell.Right -> Alignment.TopEnd
}