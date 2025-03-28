package newsref.app.ui

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import newsref.app.*
import newsref.app.blip.core.*
import newsref.app.io.*
import newsref.app.model.*
import kotlin.time.Duration.Companion.days

class HostModel(
    private val route: HostRoute,
    private val store: HostStore = HostStore()
) : StateModel<HostState>(HostState()) {
    init {
        viewModelScope.launch {
            val host = store.readHost(route.hostId)
            val sources = store.readHostSources(route.hostId, Clock.System.now() - 1.days)
                .sortedWith(compareBy<ArticleBit> { it.feedPosition }
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
    val sources: ImmutableList<ArticleBit> = persistentListOf(),
    val feeds: ImmutableList<Feed> = persistentListOf(),
)