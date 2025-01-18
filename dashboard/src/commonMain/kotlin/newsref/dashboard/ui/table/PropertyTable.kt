package newsref.dashboard.ui.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import newsref.dashboard.halfPadding
import newsref.dashboard.halfSpacing
import newsref.dashboard.innerPadding
import newsref.dashboard.roundedCorners
import newsref.dashboard.ui.theme.secondaryContainerDark

@Composable
fun <T> PropertyTable(
    name: String,
    item: T,
    properties: List<PropertyRow<T>>,
    color: Color = secondaryContainerDark.darken(.5f),
) {
    Column() {
        Text(text = name, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(halfSpacing))

        Column(
            modifier = Modifier.clip(roundedCorners)
                .border(2.dp, Color.White.copy(alpha = 0.2f), roundedCorners)
        ) {
            for (property in properties) {
                Row {
                    TableCell<T>(
                        color = color.darken(.5f),
                        width = 100,
                        alignCell = AlignCell.Right,
                        padding = halfPadding
                    ) {
                        TextCell(property.name)
                    }
                    TableCell<T>(
                        item = item,
                        color = color,
                        onClickCell = property.onClick,
                        controls = property.controls,
                        padding = halfPadding
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