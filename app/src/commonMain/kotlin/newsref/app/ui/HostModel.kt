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
            setState{ it.copy(host = host) }
        }
    }
}

data class HostState(
    val host: Host? = null
)