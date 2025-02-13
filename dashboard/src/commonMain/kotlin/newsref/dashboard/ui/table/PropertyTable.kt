package newsref.dashboard.ui.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import newsref.dashboard.halfPadding
import newsref.dashboard.halfSpacing
import newsref.dashboard.roundedCorners
import newsref.dashboard.ui.theme.secondaryContainerDark
import newsref.dashboard.utils.*

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
                Row(
                    modifier = Modifier.height(IntrinsicSize.Max)
                        .background(color.darken(.5f))
                ) {
                    Box (
                        contentAlignment = Alignment.TopEnd,
                        modifier = Modifier.width(100.dp)
                            .padding(halfPadding)
                    ) {
                        TextCell(property.name)
                    }
                    Row(
                        modifier = Modifier
                            .background(color = color)
                            .padding(halfPadding)
                    ) {
                        Box(
                            modifier = Modifier.weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .modifyIfNotNull(property.onClick) { this.clickable(onClick = { it(item) }) }
                        ) {
                            property.content(item)
                        }
                        CellControlRow(
                            item = item,
                            controls = property.controls,
                        )
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

fun <T> PropertyRow<T>.addControl(icon: ImageVector, toolTip: ToolTip? = null, block: (T) -> Unit) =
    this.copy(controls = this.controls + ActionControl(icon, toolTip, block))

fun <T> textRow(
    name: String,
    text: String?,
    lines: Int = 3,
    vararg controls: CellControl<T>,
    onClick: ((T) -> Unit)? = null
) = PropertyRow<T>(
    name = name,
    onClick = onClick,
    controls = listOf<CellControl<T>>(
        copyText { text }
    ) + controls
) {
    TextCell(text, lines = lines)
}