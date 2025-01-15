package newsref.dashboard.ui.table

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import newsref.dashboard.ui.theme.primaryContainerDark
import newsref.dashboard.utils.modifyIfNotNull

@Composable
fun <T> DataTable(
    name: String,
    rows: List<DataRow<T>>,
    columns: List<TableColumn<T>>,
    color: Color = primaryContainerDark.darken(.5f),
    onClickRow: ((T) -> Unit)? = null
) {
    LazyColumn {
        item {
            Text(text = name, style = MaterialTheme.typography.headlineSmall)
        }
        item {
            Row {
                val bgColor = color.darken(.5f)
                for (column in columns) {
                    TableCell<T>(column.width, color = bgColor) {
                        TextCell(text = column.name)
                    }
                }
            }
        }

        items(rows) { row ->
            ItemRow(
                item = row.item,
                columns = columns,
                color = if (row.isNew) color.scaleBrightness(.2f) else color,
                onClickRow = onClickRow
            )
        }
    }
}

@Composable
fun <T> ItemRow(
    item: T,
    columns: List<TableColumn<T>>,
    color: Color,
    onClickRow: ((T) -> Unit)?
) {
    Row(
        modifier = Modifier
            .modifyIfNotNull(onClickRow) { this.clickable { it(item) } }
    ) {
        for (column in columns) {
            TableCell<T>(width = column.width, item = item, color = color, onClickCell = column.onClickCell) {
                column.content(item)
            }
        }
    }
}

data class DataRow<T>(
    val isNew: Boolean,
    val item: T,
)

fun <T> Iterable<T>.toRows(isNew: (T) -> Boolean,) = this.map {
    DataRow(isNew = isNew(it), item = it)
}

data class TableColumn<T>(
    val name: String,
    val width: Int,
    val onClickCell: ((T) -> Unit)? = null,
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
