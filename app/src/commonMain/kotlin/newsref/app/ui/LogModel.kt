package newsref.app.ui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import newsref.app.pond.core.StateModel
import newsref.app.io.LogStore
import newsref.model.data.Log
import newsref.model.data.LogKey

class LogModel(
    private val key: LogKey,
    private val store: LogStore = LogStore()
) : StateModel<LogState>(LogState()) {
    init {
        viewModelScope.launch {
            val logs = store.readLogs(key)
            setState { it.copy(logs = logs) }
        }
    }
}

data class LogState(
    val logs: List<Log> = emptyList()
)