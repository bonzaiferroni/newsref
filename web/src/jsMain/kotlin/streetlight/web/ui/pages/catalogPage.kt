package streetlight.web.ui.pages

import io.kvision.core.Container
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import streetlight.web.core.AppContext
import streetlight.web.core.PortalEvents
import streetlight.web.core.ViewModel

fun Container.catalogPage(context: AppContext): PortalEvents? {
    val catalogModel = CatalogModel()
    return null
}

class CatalogModel(): ViewModel() {
    private val _state = MutableStateFlow(CatalogState())
    val state = _state.asStateFlow()
}

class CatalogState() {

}