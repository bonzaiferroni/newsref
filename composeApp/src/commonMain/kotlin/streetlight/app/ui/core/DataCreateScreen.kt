package streetlight.app.ui.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import streetlight.app.chopui.Scaffold

abstract class DataCreateScreen <Data, Model: DataCreateModel<Data, DataCreateState<Data>>>(
    private val onDataCreate: ((newData: Data) -> Unit)?
) : Screen {

    @Composable
    abstract fun provideModel(): DataCreateModel<Data, DataCreateState<Data>>

    @Composable
    abstract fun ColumnScope.DataContent()

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = provideModel()
        val state by screenModel.state

        LaunchedEffect(state.isFinished) {
            if (state.isFinished) {
                navigator?.pop()
                onDataCreate?.invoke(state.item)
            }
        }

        Scaffold("Add Area", navigator) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    DataContent()
                    Button(onClick = screenModel::createData) {
                        Text("Add Area")
                    }
                }
            }
        }
    }
}