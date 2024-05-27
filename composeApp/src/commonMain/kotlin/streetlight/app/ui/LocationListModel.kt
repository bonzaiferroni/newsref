package streetlight.app.ui

import streetlight.app.ui.abstract.UiModel
import streetlight.app.ui.abstract.UiState
import streetlight.model.Location

class LocationListModel(

) : UiModel<LocationListState>(LocationListState()) {

}

data class LocationListState(
    val locations: List<Location> = emptyList(),
) : UiState