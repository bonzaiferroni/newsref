package newsref.app.ui

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import newsref.app.*
import newsref.app.blip.controls.*
import newsref.app.blip.core.*
import newsref.app.io.*
import newsref.app.model.*
import newsref.model.utils.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

class HostModel(
    private val route: HostRoute,
    private val store: HostStore = HostStore()
): StateModel<HostState>(HostState()) {
    init {
        viewModelScope.launch {
            val host = store.readHost(route.hostId)
            val sources = store.readFeedSources(route.hostId, Clock.System.now() - 1.days).toImmutableList()
            setState{ it.copy(host = host, sources = sources) }
        }
    }

    fun changeTab(tab: String) {
        setState { it.copy(tab = tab) }
    }
}

data class HostState(
    val host: Host? = null,
    val tab: String? = null,
    val sources: ImmutableList<SourceBit> = persistentListOf()
)