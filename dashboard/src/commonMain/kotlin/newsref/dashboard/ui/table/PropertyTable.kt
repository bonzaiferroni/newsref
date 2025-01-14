package newsref.dashboard.ui.table

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

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
            Column(modifier = Modifier.width(IntrinsicSize.Max)) {
                for (property in properties) {
                    TableCell<T>(color = color.darken(.5f)) {
                        TextCell(property.name)
                    }
                }
            }
            Column {
                for (property in properties) {
                    TableCell<T>(
                        color = color,
                        item = item,
                        onClickCell = property.onClick,
                        controls = property.controls
                    ) {
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
    val controls: (List<CellControls<T>>)? = null,
    val content: @Composable (T) -> Unit
)

data class CellControls<T>(
    val icon: ImageVector,
    val onClick: (T) -> Unit,
)