package streetlight.app.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import streetlight.model.Location

class LocationListModel(

) : UiModel<LocationListState>(LocationListState()) {

}

data class LocationListState(
    val locations: List<Location> = emptyList(),
) : UiState