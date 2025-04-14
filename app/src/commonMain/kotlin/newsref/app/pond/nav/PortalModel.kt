package newsref.app.pond.nav

import newsref.app.pond.core.StateModel

class PortalModel: StateModel<PortalState>(PortalState()) {

    fun setHoverText(text: String) {
        setState { it.copy(hoverText = text) }
    }

    fun setBottomBarIsVisible(isVisible: Boolean) {
        setState { it.copy(bottomBarIsVisible = isVisible) }
    }
}

data class PortalState(
    val hoverText: String = "",
    val bottomBarIsVisible: Boolean = true,
)