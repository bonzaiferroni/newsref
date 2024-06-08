package streetlight.app.ui.core

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import moe.tlaster.precompose.navigation.Navigator
import streetlight.app.chopui.BoxScaffold

@Composable
fun <Data> DataList(
    title: String,
    items: List<Data>,
    provideName: (Data) -> String,
    floatingAction: () -> Unit,
    navigator: Navigator?,
    onClick: ((Data) -> Unit)? = null
) {
    BoxScaffold(
        title = title,
        navigator = navigator,
        floatingAction = floatingAction
    ) {
        LazyColumn {
            items(items) {
                Row {
                    if (onClick != null) {
                        Button(onClick = { onClick(it) }) {
                            Text(provideName(it))
                        }
                    } else {
                        Text(provideName(it))
                    }

                }
            }
        }
    }
}