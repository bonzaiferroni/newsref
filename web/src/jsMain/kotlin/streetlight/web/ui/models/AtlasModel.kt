package streetlight.web.ui.models

import streetlight.model.core.Location
import streetlight.web.core.StateModel
import streetlight.web.io.stores.AtlasStore

class AtlasModel(
    private val atlasStore: AtlasStore = AtlasStore()
) : StateModel<AtlasState>(AtlasState()) {

    var locations by StateDelegate({ it.locations}, { s, v -> s.copy(locations = v) })
    var newLocation by StateDelegate({ it.newLocation}, { s, v -> s.copy(newLocation = v) })

    suspend fun refresh() {
        locations = atlasStore.getAll()
    }
}

data class AtlasState(
    val locations: List<Location> = emptyList(),
    val newLocation: Location = Location(),
)