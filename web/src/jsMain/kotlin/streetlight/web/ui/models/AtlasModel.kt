package streetlight.web.ui.models

import streetlight.model.core.Location
import streetlight.web.core.StateModel

class AtlasModel(
    // private val locationStore = LocationStore()
) : StateModel<AtlasState>(AtlasState()) {
    suspend fun refresh() {

    }
}

data class AtlasState(
    val locations: List<Location> = emptyList(),
    val newLocation: Location = Location(),
)