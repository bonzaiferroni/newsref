package newsref.app.ui

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import newsref.app.*
import newsref.app.pond.core.*
import newsref.app.io.*
import newsref.model.data.Feed
import newsref.model.data.Host
import newsref.model.data.PageLite
import kotlin.time.Duration.Companion.days

class HostModel(
    private val route: HostRoute,
    private val store: HostStore = HostStore()
) : StateModel<HostState>(HostState()) {
    init {
        viewModelScope.launch {
            val host = store.readHost(route.hostId)
            val sources = store.readHostSources(route.hostId, Clock.System.now() - 1.days)
                .sortedWith(compareBy<PageLite> { it.feedPosition }
                    .thenByDescending { it.score }
                    .thenByDescending { it.existedAt })
                .toImmutableList()
            val feeds = store.readHostFeeds(route.core).toImmutableList()
            setState { it.copy(host = host, sources = sources, feeds = feeds) }
        }
    }

    fun changeTab(tab: String) {
        setState { it.copy(tab = tab) }
    }
}

data class HostState(
    val host: Host? = null,
    val tab: String? = null,
    val sources: ImmutableList<PageLite> = persistentListOf(),
    val feeds: ImmutableList<Feed> = persistentListOf(),
)