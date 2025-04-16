package newsref.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Popup
import pondui.ui.controls.Text
import pondui.ui.theme.Pond
import pondui.ui.theme.ProvideSkyColors
import kotlinx.collections.immutable.ImmutableList

@Composable
fun <T> DropMenu(
    selected: T,
    items: ImmutableList<Pair<T, String>>,
    modifier: Modifier = Modifier,
    onSelect: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ProvideSkyColors {
        Box(
            modifier = modifier
        ) {
            val selected = items.first { it.first == selected }
            DropMenuItem(selected.second) { expanded = true }
            if (expanded) {
                Popup(onDismissRequest = { expanded = false }) {
                    Column(
                        modifier = Modifier.width(IntrinsicSize.Max)
                    ) {
                        for (item in items) {
                            DropMenuItem(
                                label = item.second,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                onSelect(item.first)
                                expanded = false
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DropMenuItem(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Text(
        text = label,
        style = TextStyle(textAlign = TextAlign.Center),
        modifier = modifier
            .background(Pond.colors.primary)
            .clickable(onClick = onClick)
            .padding(Pond.ruler.halfPadding)
    )
}