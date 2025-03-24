package newsref.app.blip.controls

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList


@Composable
fun <T> RadioGroup(
    selectedValue: T,
    onOptionSelected: (T) -> Unit,
    composeOptions: () -> ImmutableList<RadioContent<T>>
) {
    val options = remember { composeOptions() }

    Column(Modifier.selectableGroup()) {
        options.forEach { (option, content) ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = option.value == selectedValue,
                        onClick = { onOptionSelected(option.value) }
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
                        color = if (option.value == selectedValue) Color.Blue else Color.Gray,
                        style = if (option.value == selectedValue) Fill else Stroke(2.dp.toPx())
                    )
                }
                Spacer(Modifier.width(8.dp))
                content()
            }
        }
    }
}

data class RadioContent<T>(
    val option: RadioOption<T>,
    val content: @Composable () -> Unit,
)

data class RadioOption<T>(
    val label: String,
    val value: T
)