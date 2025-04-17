package newsref.app.ui

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import newsref.app.*
import pondui.ui.core.*
import newsref.app.io.*
import newsref.model.data.Host
import pondui.KeyStore

class HostFeedModel(
    route: HostFeedRoute,
    private val store: HostStore = HostStore(),
    private val keyStore: KeyStore = KeyStore(),
) : StateModel<HostFeedState>(HostFeedState()) {

    private var prefs = keyStore.readObjectOrNull() ?: HostFeedPrefs()

    init {
        refreshHosts()
    }

    fun togglePin(hostId: Int) {
        prefs = if (prefs.pinnedIds.contains(hostId))
            prefs.copy(pinnedIds = prefs.pinnedIds - hostId)
        else
            prefs.copy(pinnedIds = prefs.pinnedIds + hostId)
        keyStore.writeObject(prefs)
        refreshHosts()
    }

    fun changeSearchText(text: String) {
        setState { it.copy(searchText = text) }
        refreshHosts()
    }

    private fun refreshHosts() {
        viewModelScope.launch {
            val pinnedHosts = store.readPinnedHosts(prefs.pinnedIds).toImmutableList()
            val search = stateNow.searchText
            val hosts = (if (search.isNotBlank()) store.searchHosts(search) else store.readTopHosts())
                .filter { !prefs.pinnedIds.contains(it.id) }.toImmutableList()
            setState { it.copy(pinnedHosts = pinnedHosts, hosts = hosts) }
        }
    }
}

data class HostFeedState(
    val pinnedHosts: ImmutableList<Host> = persistentListOf(),
    val hosts: ImmutableList<Host> = persistentListOf(),
    val searchText: String = "",
)

@Serializable
data class HostFeedPrefs(
    val pinnedIds: Set<Int> = emptySet()
)