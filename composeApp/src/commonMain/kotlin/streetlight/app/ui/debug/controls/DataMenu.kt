package streetlight.app.ui.debug.controls

import androidx.compose.runtime.Composable
import streetlight.app.chop.ChopMenu

@Composable
fun <Data> DataMenu(
    item: Data?,
    items: List<Data>,
    getName: (Data) -> String,
    updateItem: (Data) -> Unit,
    onNewSelect: (() -> Unit)? = null,
) {
    ChopMenu(
        item = item,
        items = items,
        getName = getName,
        updateItem = updateItem,
        onNewSelect = onNewSelect
    )
}