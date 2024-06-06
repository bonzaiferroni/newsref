package streetlight.app.ui.data

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.kodein.rememberScreenModel
import streetlight.app.io.AreaDao
import streetlight.app.io.LocationDao
import streetlight.app.ui.core.DataListModel
import streetlight.app.ui.core.DataListScreen
import streetlight.model.Area
import streetlight.model.Location

class LocationListScreen : DataListScreen<LocationInfo>() {
    override val title = "Locations"

    override fun provideScreen(callback: (Int) -> Unit) = LocationCreatorScreen { callback(it.id) }

    @Composable
    override fun rememberModel() = rememberScreenModel<LocationListModel>()

    override fun provideName(data: LocationInfo) = "${data.location.name} (${data.area.name})"

}

class LocationListModel(
    private val locationDao: LocationDao,
    private val areaDao: AreaDao,
) : DataListModel<LocationInfo>() {

    override suspend fun fetchData(): List<LocationInfo> {
        val locations = locationDao.getAll()
        val areas = areaDao.getAll()
        return locations.map { location ->
            val area = areas.find { it.id == location.areaId } ?: error("Area not found")
            LocationInfo(location, area)
        }
    }
}

data class LocationInfo(
    val location: Location,
    val area: Area,
)