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
import newsref.model.utils.toDaysFromNow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

class HostFeedModel(
    route: HostFeedRoute,
    store: HostStore = HostStore()
): StateModel<HostFeedState>(HostFeedState()) {
    init {
        viewModelScope.launch {
            val hosts = store.readHosts().toImmutableList()
            setState { it.copy(hosts = hosts) }
        }
    }
}

data class HostFeedState(
    val hosts: ImmutableList<Host> = persistentListOf()
)