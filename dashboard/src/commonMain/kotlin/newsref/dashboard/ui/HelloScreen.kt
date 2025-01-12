package newsref.dashboard.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import newsref.dashboard.AppScreen

@Composable
fun HelloScreen(navController: NavHostController) {
    Column {
        Text("Hello cupcake!")
        Button(onClick = { navController.navigate(AppScreen.Start.route)}) {
            Text("Go to Start")
        }
    }
}