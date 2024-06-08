package streetlight.app.sql

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.koin.koinViewModel
import streetlight.app.chopui.Scaffold
import streetlight.app.chopui.addBasePadding

@Composable
fun FoodScreen() {
    val model = koinViewModel(FoodModel::class)
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