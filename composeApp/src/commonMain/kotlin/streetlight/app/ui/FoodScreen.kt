package streetlight.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import streetlight.app.chopui.Scaffold
import streetlight.app.chopui.addBasePadding

class FoodScreen : Screen {

    @Composable
    override fun Content() {
        val model = rememberScreenModel<FoodModel>()
        val state by model.state

        Scaffold("Food") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .addBasePadding()
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = state.newFood.name,
                        onValueChange = model::onNameChange,
                    )
                    Button(onClick = model::onAddFood) {
                        Text("Add")
                    }
                }
                LazyColumn {
                    items(state.foods) { food ->
                        Text(food.name)
                    }
                }
            }
        }
    }
}