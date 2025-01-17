package newsref.dashboard.ui.table

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import newsref.dashboard.halfSpacing
import newsref.dashboard.ui.theme.secondaryContainerDark

@Composable
fun <T> PropertyTable(
    name: String,
    item: T,
    properties: List<PropertyRow<T>>,
    color: Color = secondaryContainerDark.darken(.5f),
) {
    Column {
        Text(text = name, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(halfSpacing))

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
    val controls: List<CellControl<T>> = emptyList(),
    val content: @Composable (T) -> Unit
)

fun <T> PropertyRow<T>.onClick(block: (T) -> Unit) = this.copy(onClick = block)

fun <T> PropertyRow<T>.addControl(icon: ImageVector, block: (T) -> Unit) =
    this.copy(controls = this.controls + CellControl(icon, block))

data class CellControl<T>(
    val icon: ImageVector,
    val onClick: (T) -> Unit,
)