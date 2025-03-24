package newsref.app.blip.controls

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import newsref.app.blip.behavior.animateFloat
import newsref.app.blip.theme.Blip


@Composable
fun <T> RadioGroup(
    selectedValue: T,
    onOptionSelected: (T) -> Unit,
    composeOptions: () -> List<RadioContent<T>>
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
                val circleColor = Blip.localColors.content.copy(.8f)
                val indicatorColor = Blip.colors.accent
                val size = 16
                val padding = 4
                val targetIndicatorSize = when {
                    option.value == selectedValue -> (size / 2f) - 3
                    else -> 0f
                }
                val indicatorSize = animateFloat(targetIndicatorSize)

                Canvas(
                    Modifier
                        .size((size + padding * 2).dp)
                        .padding(padding.dp)
                ) {
                    drawCircle(
                        color = circleColor,
                        style = Stroke(2.dp.toPx())
                    )
                    drawCircle(
                        radius = indicatorSize,
                        color = indicatorColor,
                        style = Fill
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