package newsref.app.ui

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import newsref.app.*
import newsref.app.blip.core.*
import newsref.app.io.*
import newsref.app.model.*

class HostFeedModel(
    route: HostFeedRoute,
    private val store: HostStore = HostStore(),
    private val keyStore: KeyStore = KeyStore(),
) : StateModel<HostFeedState>(HostFeedState()) {

    private var prefs = HostFeedPrefs()

    init {
        viewModelScope.launch {
            keyStore.readObject { HostFeedPrefs() }.collect {
                prefs = it
                refreshHosts()
            }
        }
    }

    fun togglePin(hostId: Int) {
        val modifiedPrefs = if (prefs.pinnedIds.contains(hostId))
            prefs.copy(pinnedIds = prefs.pinnedIds - hostId)
        else
            prefs.copy(pinnedIds = prefs.pinnedIds + hostId)
        keyStore.writeObject(modifiedPrefs)
    }

    fun changeSearchText(text: String) {
        setState { it.copy(searchText = text) }
        viewModelScope.launch {
            refreshHosts()
        }
    }

    private suspend fun refreshHosts() {
        val pinnedHosts = store.readPinnedHosts(prefs.pinnedIds).toImmutableList()
        val search = stateNow.searchText
        val hosts = (if (search.isNotBlank()) store.searchHosts(search) else store.readTopHosts())
            .filter { !prefs.pinnedIds.contains(it.id) }.toImmutableList()
        setState { it.copy(pinnedHosts = pinnedHosts, hosts = hosts) }
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