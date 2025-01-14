package newsref.dashboard.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import newsref.dashboard.FeedRowRoute

@Composable
fun FeedRowScreen(
    route: FeedRowRoute,
    navController: NavController,
    viewModel: FeedRowModel = viewModel { FeedRowModel(route) }
) {
    Text("Hello Feed Row!")
}

class FeedRowModel(
    route: FeedRowRoute
) : ViewModel() {

}
