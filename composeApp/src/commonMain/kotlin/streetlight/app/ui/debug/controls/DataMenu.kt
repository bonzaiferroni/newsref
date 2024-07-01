package streetlight.app.ui.debug.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

@Composable
fun <Data> DataMenu(
    item: Data?,
    items: List<Data>,
    getName: (Data) -> String,
    updateItem: (Data) -> Unit,
    onNewSelect: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            item?.let {
                Text(getName(it))
            }
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More"
                )
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onNewSelect()
                },
                text = { Text("New...") }
            )
            items.forEach { it ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        updateItem(it)
                    },
                    text = { Text(getName(it)) }
                )
            }
        }
    }
}