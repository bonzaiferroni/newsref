package newsref.web.ui.models

import kotlinx.coroutines.launch
import newsref.model.data.FeedSource
import newsref.model.dto.SourceInfo
import newsref.web.core.StateModel
import newsref.web.io.stores.SourceStore

class SourceModel(
	val id: Long,
	private val sourceStore: SourceStore = SourceStore(),
): StateModel<SourceState>(SourceState()) {
	private var source by StateDelegate({it.source}) { s, v -> s.copy(source = v) }

	init {
		viewModelScope.launch {
			refreshSource()
		}
	}

	private suspend fun refreshSource() {
		source = sourceStore.getSource(id)
	}

	fun toggleMoreInbound() {
		_state.value = _state.value.copy(showMoreInbound = !state.value.showMoreInbound)
	}

	fun toggleMoreOutbound() {
		sv = sv.copy(showMoreOutbound = !sv.showMoreOutbound)
	}
}

data class SourceState(
	val source: SourceInfo? = null,
	val showMoreInbound: Boolean = false,
	val showMoreOutbound: Boolean = false,
)