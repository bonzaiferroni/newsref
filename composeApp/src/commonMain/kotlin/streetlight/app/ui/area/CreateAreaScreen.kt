package streetlight.app.ui.area

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import streetlight.app.chopui.Scaffold

class CreateAreaScreen(
    private val onComplete: ((id: Int) -> Unit)?
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = rememberScreenModel<CreateAreaModel>()
        val state by screenModel.state

        LaunchedEffect(state.isFinished) {
            if (state.isFinished) {
                navigator?.pop()
                onComplete?.invoke(state.area.id)
            }
        }

        Scaffold("Add Area", navigator) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    TextField(
                        value = state.area.name,
                        onValueChange = screenModel::updateName,
                        label = { Text("Name") }
                    )
                    Button(onClick = screenModel::addArea) {
                        Text("Add Area")
                    }
                    Text(state.result)
                }
            }
        }
    }
}