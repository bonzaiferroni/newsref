package newsref.dashboard.ui


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun <T> TableView(
    columns: List<TableColumn<T>>,
    items: List<T>,
) {
    Column {
        Row {
            for (column in columns) {
                TableCell(
                    text = column.name,
                    width = column.width
                )
            }
        }
        items.forEach { item ->
            Row {
                for (column in columns) {
                    TableCell(
                        text = column.getValue(item),
                        width = column.width
                    )
                }
            }
        }
    }
}

@Composable
fun TableCell(
    text: String,
    width: Int
) {
    Text(
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.width(width.dp)
    )
}

data class TableColumn<T>(
    val name: String,
    val width: Int,
    val getValue: (T) -> String
)