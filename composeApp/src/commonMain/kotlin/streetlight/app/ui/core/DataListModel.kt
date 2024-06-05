package streetlight.app.ui.core

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class DataListModel <Data> : UiModel<ListDataState<Data>>(ListDataState()) {

    abstract suspend fun fetchData(): List<Data>

    init {
        refresh()
    }

    fun refresh(newId: Int = 0) {
        screenModelScope.launch(Dispatchers.IO) {
            val items = fetchData()
            sv = sv.copy(items = items)
        }
    }
}

data class ListDataState <Data> (
    val items: List<Data> = emptyList()
) : UiState