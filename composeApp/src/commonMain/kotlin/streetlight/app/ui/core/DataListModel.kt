package streetlight.app.ui.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope

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