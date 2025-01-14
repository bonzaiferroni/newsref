package newsref.dashboard.ui.table

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun <T> DataTable(
    name: String,
    items: List<T>,
    columns: List<TableColumn<T>>,
    color: Color = Color(.5f, .6f, .7f, 1f)
) {
    LazyColumn {
        item {
            Text(text = name)
        }
        item {
            Row {
                val baseColor = color.darken(.8f)
                val altColor = color.darken(.7f)
                var alternateColor = false

                for (column in columns) {
                    val bgColor = if (alternateColor) altColor else baseColor
                    TableCell(column.width, bgColor) {
                        TextCell(text = column.name)
                    }
                    alternateColor = !alternateColor
                }
            }
        }

        var alternateColumnBg = false
        var alternateRowBg = false
        val cellColors = mapOf(
            false to mapOf(false to color, true to color.darken(.1f)),
            true to mapOf(false to color.darken(.2f), true to color.darken(.3f))
        )

        items(items) { item ->
            Row {
                for (column in columns) {
                    val bgColor = cellColors.getValue(alternateRowBg).getValue(alternateColumnBg)

                    TableCell(column.width, bgColor) {
                        column.content(item)
                    }
                    alternateColumnBg = !alternateColumnBg
                }
                alternateRowBg = !alternateRowBg
            }
        }
    }
}

data class TableColumn<T>(
    val name: String,
    val width: Int,
    val content: @Composable (T) -> Unit
)

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