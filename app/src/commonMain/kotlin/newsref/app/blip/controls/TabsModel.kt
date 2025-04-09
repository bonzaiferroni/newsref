package newsref.app.blip.controls

import newsref.app.blip.core.StateModel

class TabsModel(initialTab: String?): StateModel<TabsState>(TabsState(initialTab)) {
    fun setTab(tab: String?) {
        setState { it.copy(tab = tab) }
    }
}

data class TabsState(
    val tab: String? = null,
)