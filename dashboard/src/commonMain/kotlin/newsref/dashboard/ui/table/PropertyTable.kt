package newsref.dashboard.ui.table

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun <T> PropertyTable(
    name: String,
    item: T,
    properties: List<PropertyRow<T>>,
    color: Color = Color(.2f, .22f, .24f, 1f),
) {
    Column {
        Text(text = name, style = MaterialTheme.typography.headlineSmall)

        Row {
            Column {
                for (property in properties) {
                    TableCell<T>(color = color.darken(.5f)) {
                        TextCell(property.name)
                    }
                }
            }
            Column {
                for (property in properties) {
                    TableCell<T>(color = color, item = item, onClickCell = property.onClick) {
                        property.content(item)
                    }
                }
            }
        }
    }
}

data class PropertyRow<T>(
    val name: String,
    val onClick: ((T) -> Unit)? = null,
    val content: @Composable (T) -> Unit
)