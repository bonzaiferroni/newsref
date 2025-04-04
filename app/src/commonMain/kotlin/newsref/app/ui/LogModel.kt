package newsref.app.ui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import newsref.app.blip.core.StateModel
import newsref.app.io.LogStore
import newsref.app.model.Log
import newsref.model.dto.LogKey

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