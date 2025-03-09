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
): StateModel<HostFeedState>(HostFeedState()) {

    private var preferences = HostFeedPrefs()

    init {
        viewModelScope.launch {
            keyStore.readObject { HostFeedPrefs() }.collect { prefs ->
                preferences = prefs
                val pinnedHosts = store.readPinnedHosts(prefs.pinnedIds).toImmutableList()
                val hosts = store.readTopHosts().filter{ !prefs.pinnedIds.contains(it.id) }.toImmutableList()
                setState { it.copy(pinnedHosts = pinnedHosts, hosts = hosts) }
            }
        }
    }

    fun togglePin(hostId: Int) {
        val prefs = if (preferences.pinnedIds.contains(hostId))
            preferences.copy(pinnedIds = preferences.pinnedIds - hostId)
        else
            preferences.copy(pinnedIds = preferences.pinnedIds + hostId)
        keyStore.writeObject(prefs)
    }
}

data class HostFeedState(
    val pinnedHosts: ImmutableList<Host> = persistentListOf(),
    val hosts: ImmutableList<Host> = persistentListOf(),
)

@Serializable
data class HostFeedPrefs(
    val pinnedIds: Set<Int> = emptySet()
)