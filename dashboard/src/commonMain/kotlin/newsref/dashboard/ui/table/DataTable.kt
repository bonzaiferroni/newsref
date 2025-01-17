package newsref.dashboard.ui.table

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.dashboard.halfSpacing
import newsref.dashboard.innerPadding
import newsref.dashboard.roundedCorners
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
    onClickRow: ((T) -> Unit)? = null
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(halfSpacing)
    ) {
        item {
            Text(text = name, style = MaterialTheme.typography.headlineSmall)
        }
        item {
            val bgColor = color.darken(.5f)
            FlowRow(
                modifier = Modifier
                    .border(2.dp, Color.White.copy(alpha = 0.2f), roundedCorners)
                    .clip(roundedCorners)
                    .background(bgColor)
                    .padding(innerPadding)
            ) {
                for (column in columns) {
                    TableCell<T>(
                        width = column.width,
                        color = bgColor,
                        alignContent = column.alignContent
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

            var alpha = remember { Animatable(1f) } // Start fully transparent

            LaunchedEffect(item) {
                if (isNew != null && isNew(item)) alpha.snapTo(0f)
                alpha.animateTo(1f, animationSpec = tween(1000, easing = FastOutSlowInEasing))
            }

            FlowRow(
                modifier = Modifier.modifyIfNotNull(onClickRow) { this.clickable { it(item) } }
                    .alpha(alpha.value)
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
                        alignContent = column.alignContent,
                        onClickCell = column.onClickCell
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
    val alignContent: AlignContent? = null,
    val onClickCell: ((T) -> Unit)? = null,
    val content: @Composable (T) -> Unit
)

fun <T> TableColumn<T>.onClick(block: (T) -> Unit) = this.copy(onClickCell = block)

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