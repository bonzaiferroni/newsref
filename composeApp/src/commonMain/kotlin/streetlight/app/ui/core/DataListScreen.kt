package streetlight.app.ui.core

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import streetlight.app.chopui.BoxScaffold

@Composable
fun <Data> DataList(
    title: String,
    items: List<Data>,
    provideName: (Data) -> String,
    floatingAction: () -> Unit,
    navigator: Navigator?,
) {
    BoxScaffold(
        title = title,
        navigator = navigator,
        floatingAction = floatingAction
    ) {
        LazyColumn {
            items(items) {
                Row {
                    Text(provideName(it))
                }
            }
        }
    }
}