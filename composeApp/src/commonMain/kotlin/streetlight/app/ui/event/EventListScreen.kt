package streetlight.app.ui.event

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.kodein.rememberScreenModel
import streetlight.app.data.EventDao
import streetlight.app.ui.core.DataListModel
import streetlight.app.ui.core.DataListScreen
import streetlight.dto.EventInfo
import streetlight.model.Event

class EventListScreen : DataListScreen<EventInfo>() {

    override val title = "Events"
    override fun provideScreen(callback: (Int) -> Unit) = EventCreatorScreen { callback(it.id) }
    override fun provideName(data: EventInfo) = "${data.locationName} (${data.areaName})"
    @Composable
    override fun rememberModel() = rememberScreenModel<EventListModel>()
}

class EventListModel(
    private val eventDao: EventDao,
) : DataListModel<EventInfo>() {
    override suspend fun fetchData() = eventDao.getAllInfo()
}