package streetlight.app.ui.debug.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.tlaster.precompose.navigation.Navigator
import org.jetbrains.compose.ui.tooling.preview.Preview
import streetlight.app.chop.BoxScaffold
import streetlight.app.ui.theme.AppTheme

@Composable
fun <Data> DataList(
    title: String,
    items: List<Data>,
    provideName: (Data) -> String,
    floatingAction: () -> Unit,
    navigator: Navigator?,
    onEdit: ((Data) -> Unit),
    onDelete: ((Data) -> Unit),
) {
    BoxScaffold(
        title = title,
        navigator = navigator,
        floatingAction = floatingAction,
    ) {
        LazyColumn(
            modifier = Modifier.widthIn(max = 400.dp)
        ) {
            items(items) {
                DataRow(it, provideName, onEdit, onDelete)
            }
        }
    }
}

@Composable
fun <Data> DataRow(
    data: Data,
    provideName: (Data) -> String,
    onClick: ((Data) -> Unit),
    onDelete: ((Data) -> Unit),
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        Text(provideName(data))
        Spacer(Modifier.weight(1f))
        Button(onClick = { onClick(data) }) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
        }
        Button(
            onClick = { onDelete(data) }, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}

@Preview
@Composable
fun DataListPreview() {
    AppTheme {
        DataRow(
            data = "Data",
            provideName = { it },
            onClick = {},
            onDelete = {})
    }
}