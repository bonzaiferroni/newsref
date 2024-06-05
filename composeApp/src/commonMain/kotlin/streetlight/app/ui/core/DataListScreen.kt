package streetlight.app.ui.core

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import streetlight.app.chopui.BoxScaffold

abstract class DataListScreen<Data> : Screen {
    abstract val title: String
    abstract fun provideScreen(callback: (Int) -> Unit): Screen
    abstract fun provideName(data: Data): String

    @Composable
    abstract fun rememberModel(): DataListModel<Data>

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = rememberModel()
        val state by screenModel.state
        BoxScaffold(
            title = title,
            navigator = navigator,
            floatingAction = { navigator?.push(provideScreen(screenModel::refresh)) }
        ) {
            LazyColumn {
                items(state.items) {
                    Row {
                        Text(provideName(it))
                    }
                }
            }
        }
    }
}