package streetlight.app.ui.debug.controls

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState

abstract class DataListModel <Data> : UiModel<ListDataState<Data>>(ListDataState()) {

    abstract suspend fun fetchData(): List<Data>

    init {
        refresh()
    }

    fun refresh(newId: Int = 0) {
        viewModelScope.launch(Dispatchers.IO) {
            val items = fetchData()
            sv = sv.copy(items = items)
        }
    }
}

data class ListDataState <Data> (
    val items: List<Data> = emptyList()
) : UiState