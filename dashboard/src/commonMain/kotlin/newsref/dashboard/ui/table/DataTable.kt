package newsref.dashboard.ui.table

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.*
import kotlinx.datetime.*
import newsref.app.utils.modifyIfNotNull
import newsref.dashboard.*
import newsref.dashboard.ui.theme.primaryContainerDark
import newsref.dashboard.utils.*
import newsref.model.core.*

@Composable
fun <Item> DataTable(
    name: String,
    items: List<Item>,
    columnGroups: List<ColumnGroup<Item>>,
    getKey: ((Item) -> Any)? = null,
    isNew: ((Item) -> Boolean)? = null,
    color: Color = primaryContainerDark.darken(.5f),
    searchText: String = "",
    scrollId: Long? = null,
    isSelected: ((Long, Item) -> Boolean)? = null,
    onSorting: ((Sorting) -> Unit)? = null,
    onSearch: ((String) -> Unit)? = null,
    glowFunction: ((Item) -> Float?)? = null,
    onFirstVisibleIndex: ((Int) -> Unit)? = null,
    onClickRow: ((Item) -> Unit)? = null
) {
    var sorting by remember { mutableStateOf<Sorting>(null to null) }

    fun updateSort(sort: DataSort) {
        val (currentSort, currentDirection) = sorting
        sorting = if (currentSort == sort) {
            when (currentDirection) {
                SortDirection.Ascending -> null to null
                SortDirection.Descending -> currentSort to SortDirection.Ascending
                null -> error("")
            }
        } else {
            sort to SortDirection.Descending
        }
    }

    LaunchedEffect(sorting) {
        onSorting?.invoke(sorting)
    }

    // Create a LazyListState
    val listState = rememberLazyListState()

    LaunchedEffect(scrollId) {
        if (scrollId != null && isSelected != null) {
            val index = items.indexOfFirst { isSelected(scrollId, it) }
            listState.animateScrollToItem(index = index) // Scroll to item 10 smoothly
        }
    }

    // Observe scroll position using derived state
    val firstVisibleIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex }
    }

    LaunchedEffect(firstVisibleIndex) {
        if (onFirstVisibleIndex != null) onFirstVisibleIndex(firstVisibleIndex)
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(halfSpacing),
        state = listState,
    ) {
        item {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = name, style = MaterialTheme.typography.headlineSmall)
                onSearch?.let {
                    BasicTextField(
                        value = searchText,
                        onValueChange = it,
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier.width(200.dp)
                            .clip(roundedCorners)
                            .border(2.dp, Color.White.copy(alpha = .2f), roundedCorners)
                            .padding(halfPadding)
                    )
                }
            }
        }
        item {
            val bgColor = color.darken(.5f)
            Column(
                modifier = Modifier
                    .border(2.dp, Color.White.copy(alpha = 0.1f), roundedHeader)
                    .clip(roundedHeader)
                    .background(bgColor)
                    .padding(innerPadding)
            ) {
                for (columnGroup in columnGroups) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (column in columnGroup) {
                            if (!column.isVisible) continue
                            val onClick: (() -> Unit)? = if (onSorting != null)
                                column.sort?.let { sort -> { updateSort(sort) } } else null
                            val toolTip = if (onClick != null)
                                ToolTip("Sort by ${column.sort?.name}", TipType.Action) else null

                            TableCell<Item>(
                                width = column.width,
                                color = bgColor,
                                alignCell = column.align,
                                weight = column.weight,
                                onClickCell = onClick,
                                toolTip = column.headerTip ?: toolTip,
                                modifier = Modifier.alpha(column.alpha)
                            ) {
                                TextCell(text = column.name)
                            }
                        }
                    }
                }
            }
        }

        this.items(items, getKey) { item ->
            val itemIsSelected = if (isSelected != null && scrollId != null) {
                isSelected(scrollId, item)
            } else { false }

            val bgColor = if (itemIsSelected) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                glowFunction?.let { function ->
                    function(item)?.let { color.scaleBrightness(it * .5f) }
                } ?: color
            }
            val initialValue = if (isNew != null && isNew(item)) 0f else 1f
            var alpha = remember { Animatable(initialValue) } // Start fully transparent

            LaunchedEffect(item) {
                if (isNew != null && isNew(item)) alpha.snapTo(0f)
                alpha.animateTo(1f, animationSpec = tween(500, delayMillis = 200, easing = EaseOut))
            }

            Column(
                modifier = Modifier.animateItem()
                    .alpha(alpha.value)
                    .offset(x = ((1 - alpha.value) * 10).dp)
                    .border(2.dp, Color.White.copy(alpha = 0.1f), roundedCorners)
                    .clip(roundedCorners)
                    .modifyIfNotNull(onClickRow) { this.clickable { it(item) } }
                    .background(bgColor)
                    .padding(innerPadding)
            ) {
                for (columnGroup in columnGroups) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (column in columnGroup) {
                            if (!column.isVisible) continue
                            TableCell<Item>(
                                width = column.width,
                                item = item,
                                color = bgColor,
                                alignCell = column.align,
                                weight = column.weight,
                                toolTip = column.cellTip,
                                onClickCell = column.onClickCell?.let { onClick -> { onClick(item) }},
                                controls = column.controls,
                                modifier = Modifier.alpha(column.alpha)
                            ) {
                                column.content(item)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class TableColumn<T>(
    val name: String,
    val width: Int? = null,
    val align: AlignCell? = null,
    val alpha: Float = 1f,
    val weight: Float? = null,
    val sort: DataSort? = null,
    val isVisible: Boolean = true,
    val headerTip: ToolTip? = null,
    val cellTip: ToolTip? = null,
    val onClickCell: ((T) -> Unit)? = null,
    val controls: List<CellControl<T>> = emptyList(),
    val content: @Composable (T) -> Unit
)

typealias ColumnGroup<T> = List<TableColumn<T>>

fun <T> columns(vararg elements: TableColumn<T>) = elements.asList()
fun <T> groups(vararg elements: ColumnGroup<T>) = elements.asList()


fun <T> TableColumn<T>.onClick(toolTip: ToolTip? = null, block: (T) -> Unit) =
    this.copy(onClickCell = block, cellTip = toolTip)

fun <T> TableColumn<T>.addControl(icon: ImageVector, toolTip: ToolTip? = null, block: (T) -> Unit) =
    this.copy(controls = this.controls + ActionControl(icon, toolTip, block))

fun <T> TableColumn<T>.addControl(control: ActionControl<T>) =
    this.copy(controls = this.controls + control)

fun Color.darken(factor: Float = 0.8f) = this.scaleBrightness(-factor)

fun Color.scaleBrightness(factor: Float): Color {
    val scaleFactor = (factor + 1).coerceIn(0f, 2f)
    return Color(
        red = this.red * scaleFactor,
        green = this.green * scaleFactor,
        blue = this.blue * scaleFactor,
        alpha = this.alpha
    )
}

fun Color.addBrightness(factor: Float) = Color(
    red = this.red + factor,
    green = this.green + factor,
    blue = this.blue + factor,
)

fun glowOverDay(instant: Instant?) = instant?.let { (24 - (Clock.System.now() - it).inWholeHours) / 24f }
fun glowOverHour(instant: Instant?) = instant?.let { (60 - (Clock.System.now() - it).inWholeMinutes) / 60f }
fun glowOverMin(instant: Instant?) = instant?.let { (60 - (Clock.System.now() - it).inWholeMinutes) / 60f }

data class SwitchArg(
    val name: String,
    val isOn: Boolean,
    val onSwitch: (Boolean) -> Unit
)

inline fun <T> List<T>.firstOrNullIndexed(predicate: (index: Int, T) -> Boolean): Pair<Int, T>? {
    forEachIndexed { index, item ->
        if (predicate(index, item)) return index to item
    }
    return null
}