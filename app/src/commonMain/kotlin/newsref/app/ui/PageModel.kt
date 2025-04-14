package newsref.app.ui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import newsref.app.PageRoute
import newsref.app.pond.core.StateModel
import newsref.app.io.HostStore
import newsref.app.io.PageStore
import newsref.model.data.Host
import newsref.model.data.Page

class PageModel(
    route: PageRoute,
    private val pageStore: PageStore = PageStore(),
    private val hostStore: HostStore = HostStore(),
): StateModel<PageState>(PageState()) {
    init {
        viewModelScope.launch {
            val page = pageStore.readPage(route.pageId)
            setState { it.copy(page = page) }
            val host = hostStore.readHost(page.hostId)
            setState { it.copy(host = host) }
        }
    }

    fun onChangeTab(tab: String) {
        setState { it.copy(tab = tab) }
    }
}

data class PageState(
    val tab: String? = null,
    val page: Page? = null,
    val host: Host? = null,
)