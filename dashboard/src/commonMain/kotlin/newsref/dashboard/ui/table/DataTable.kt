package newsref.dashboard.ui.table

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.dashboard.halfSpacing
import newsref.dashboard.innerPadding
import newsref.dashboard.roundedCorners
import newsref.dashboard.roundedHeader
import newsref.dashboard.ui.theme.primaryContainerDark
import newsref.dashboard.utils.modifyIfNotNull

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun <T> DataTable(
    name: String,
    rows: List<T>,
    columns: List<TableColumn<T>>,
    isNew: ((T) -> Boolean)? = null,
    color: Color = primaryContainerDark.darken(.5f),
    glowFunction: ((T) -> Float?)? = null,
    onFirstVisibleIndex: ((Int) -> Unit)? = null,
    onClickRow: ((T) -> Unit)? = null
) {
    // Create a LazyListState
    val listState = rememberLazyListState()

    // Observe scroll position using derived state
    val firstVisibleIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex }
    }

    LaunchedEffect(firstVisibleIndex) {
        if (onFirstVisibleIndex != null) onFirstVisibleIndex(firstVisibleIndex)
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(halfSpacing),
        state = listState
    ) {
        item {
            Text(text = name, style = MaterialTheme.typography.headlineSmall)
        }
        item {
            val bgColor = color.darken(.5f)
            FlowRow(
                modifier = Modifier
                    .border(2.dp, Color.White.copy(alpha = 0.2f), roundedHeader)
                    .clip(roundedHeader)
                    .background(bgColor)
                    .padding(innerPadding)
            ) {
                for (column in columns) {
                    TableCell<T>(
                        width = column.width,
                        color = bgColor,
                        alignCell = column.alignCell,
                        weight = column.weight,
                        toolTip = column.headerTip,
                        modifier = Modifier.alpha(column.alpha)
                    ) {
                        TextCell(text = column.name)
                    }
                }
            }
        }

        items(rows) { item ->
            val bgColor =  glowFunction?.let { function ->
                function(item)?.let { color.scaleBrightness(it * .5f) }
            } ?: color
            val initialValue = if (isNew != null && isNew(item)) 0f else 1f
            var alpha = remember { Animatable(initialValue) } // Start fully transparent

            LaunchedEffect(item) {
                if (isNew != null && isNew(item)) alpha.snapTo(0f)
                alpha.animateTo(1f, animationSpec = tween(500, delayMillis = 200, easing = EaseOut))
            }

            FlowRow(
                modifier = Modifier.modifyIfNotNull(onClickRow) { this.clickable { it(item) } }
                    .alpha(alpha.value)
                    .offset(x = ((1 - alpha.value) * 10).dp)
                    .border(2.dp, Color.White.copy(alpha = 0.2f), roundedCorners)
                    .clip(roundedCorners)
                    .background(bgColor)
                    .padding(innerPadding)
            ) {
                for (column in columns) {
                    TableCell<T>(
                        width = column.width,
                        item = item,
                        color = bgColor,
                        alignCell = column.alignCell,
                        weight = column.weight,
                        onClickCell = column.onClickCell,
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

data class TableColumn<T>(
    val name: String,
    val width: Int? = null,
    val alignCell: AlignCell? = null,
    val alpha: Float = 1f,
    val weight: Float? = null,
    val headerTip: String? = null,
    val onClickCell: ((T) -> Unit)? = null,
    val controls: List<CellControl<T>> = emptyList(),
    val content: @Composable (T) -> Unit
)

fun <T> TableColumn<T>.onClick(block: (T) -> Unit) = this.copy(onClickCell = block)
fun <T> TableColumn<T>.addControl(icon: ImageVector, block: (T) -> Unit) =
    this.copy(controls = this.controls + CellControl(icon, block))

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