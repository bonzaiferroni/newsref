package streetlight.app.ui.core

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
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun <Data> DataMenu(
    navigator: Navigator?,
    item: Data?,
    items: List<Data>,
    newItemLink: String,
    getName: (Data) -> String,
    updateLocation: (Data) -> Unit,
    onNewLocation: () -> Unit,
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
                    onNewLocation()
                    navigator?.navigate(newItemLink)
                },
                text = { Text("New...") }
            )
            items.forEach { it ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        updateLocation(it)
                    },
                    text = { Text(getName(it)) }
                )
            }
        }
    }
}