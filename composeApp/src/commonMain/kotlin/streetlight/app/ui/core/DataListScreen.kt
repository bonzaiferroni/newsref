package streetlight.app.ui.core

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.navigation.Navigator
import org.jetbrains.compose.ui.tooling.preview.Preview
import streetlight.app.chopui.BoxScaffold
import streetlight.app.ui.theme.AppTheme

@Composable
fun <Data> DataList(
    title: String,
    items: List<Data>,
    provideName: (Data) -> String,
    floatingAction: () -> Unit,
    navigator: Navigator?,
    onEdit: ((Data) -> Unit),
    onDelete: ((Data) -> Unit) = {},
) {
    BoxScaffold(
        title = title,
        navigator = navigator,
        floatingAction = floatingAction
    ) {
        LazyColumn {
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
    Row {
        Text(provideName(data))
        Spacer(Modifier.weight(1f))
        Button(onClick = { onClick(data) }) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
        }
        Button(onClick = { onDelete(data) }, colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.background) ) {
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