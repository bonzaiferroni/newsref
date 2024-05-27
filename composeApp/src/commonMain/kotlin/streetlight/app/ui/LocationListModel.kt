package streetlight.app.ui

import streetlight.app.ui.model.UiModel
import streetlight.app.ui.model.UiState
import streetlight.model.Location

class LocationListModel(

) : UiModel<LocationListState>(LocationListState()) {

}

data class LocationListState(
    val locations: List<Location> = emptyList(),
) : UiState