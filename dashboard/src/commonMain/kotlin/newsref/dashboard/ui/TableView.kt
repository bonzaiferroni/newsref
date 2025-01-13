package newsref.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun <T> DataTable(
    name: String,
    items: List<T>,
    columns: List<TableColumn<T>>
) {
    Text(text = name)
    Row {
        for (column in columns) {
            Column {
                TableCell(column.width) {
                    TextCell(text = column.name)
                }
                for (item in items) {
                    TableCell(column.width) {
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
    val content: @Composable (T) -> Unit
)

@Composable
fun TableCell(
    width: Int,
    color: Color = Color(0f, 0f, 0f, 0f),
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .width(width.dp)
            .background(color = color)
    ) {
        content()
    }
}

@Composable
fun TextCell(
    text: String
) {
    Text(
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}