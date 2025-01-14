package newsref.dashboard.ui.table

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun <T> DataTable(
    name: String,
    items: List<T>,
    columns: List<TableColumn<T>>,
    color: Color = Color(.2f, .24f, .22f, 1f),
    onClickRow: (() -> Unit)? = null
) {
    LazyColumn {
        item {
            Text(text = name, style = MaterialTheme.typography.headlineSmall)
        }
        item {
            Row(
                modifier = Modifier
                    .apply { onClickRow?.let { this.clickable(onClick = it) } }
            ) {
                val bgColor = color.darken(.5f)
                for (column in columns) {
                    TableCell(column.width, bgColor) {
                        TextCell(text = column.name)
                    }
                }
            }
        }

        items(items) { item ->
            Row {
                for (column in columns) {
                    TableCell(column.width, color) {
                        column.content(item)
                    }
                }
            }
        }
    }
}

data class TableColumn<T>(
    val name: String,
    val width: Int,
    val onClickCell: (() -> Unit)? = null,
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