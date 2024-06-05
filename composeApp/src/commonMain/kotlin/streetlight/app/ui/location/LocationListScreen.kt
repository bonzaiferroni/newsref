package streetlight.app.ui.location

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import streetlight.app.chopui.BoxScaffold
import streetlight.app.chopui.Scaffold
import streetlight.app.data.AreaDao
import streetlight.app.data.LocationDao
import streetlight.app.ui.core.DataListModel
import streetlight.app.ui.core.DataListScreen
import streetlight.model.Area
import streetlight.model.Location

class LocationListScreen : DataListScreen<LocationInfo>() {
    override val title = "Locations"

    override fun provideScreen(callback: (Int) -> Unit) = LocationCreateScreen(callback)

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