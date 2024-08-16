package streetlight.app.chop

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@Composable
fun <Data> ChopMenu(
    item: Data?,
    items: List<Data>,
    getName: (Data) -> String,
    updateItem: (Data) -> Unit,
    onNewSelect: (() -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = !expanded},
            border = BorderStroke(1.dp, ButtonDefaults.outlinedButtonColors().contentColor),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                item?.let {
                    Text(getName(it))
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Open menu"
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            onNewSelect?.let {
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        it()
                    },
                    text = { Text("New...") }
                )
            }
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