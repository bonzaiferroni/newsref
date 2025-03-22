package newsref.app.blip.controls

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList


@Composable
fun <T> RadioGroup(
    selectedValue: T,
    onOptionSelected: (T) -> Unit,
    composeOptions: () -> ImmutableList<RadioOption<T>>
) {
    val options = remember { composeOptions() }

    Column(Modifier.selectableGroup()) {
        options.forEach { (value, content) ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = value == selectedValue,
                        onClick = { onOptionSelected(value) }
                    )
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Canvas(
                    Modifier
                        .size(24.dp)
                        .padding(4.dp)
                ) {
                    drawCircle(
                        color = if (value == selectedValue) Color.Blue else Color.Gray,
                        style = if (value == selectedValue) Fill else Stroke(2.dp.toPx())
                    )
                }
                Spacer(Modifier.width(8.dp))
                content()
            }
        }
    }
}

data class RadioOption<T>(
    val value: T,
    val content: @Composable () -> Unit,
)