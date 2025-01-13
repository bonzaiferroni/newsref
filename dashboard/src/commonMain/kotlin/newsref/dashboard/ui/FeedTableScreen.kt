package newsref.dashboard.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import newsref.db.services.FeedService
import newsref.model.data.Feed

@Composable
fun FeedTableScreen(
    navController: NavController,
    viewModel: FeedTableModel = viewModel { FeedTableModel() }
) {
    val state by viewModel.uiState.collectAsState()
    TableView(
        columns = listOf(
            TableColumn<Feed>("Core", 200) { it.url.core },
            TableColumn<Feed>("Url", 400) { it.url.toString() },
        ),
        items = state.feedItems
    )
}

class FeedTableModel(
    private val feedService: FeedService = FeedService()
) : ViewModel() {
    private val _uiState = MutableStateFlow(FeedTableState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(feedItems = feedService.readAll())
        }
    }
}

data class FeedTableState(
    val feedItems: List<Feed> = emptyList()
)